package com.opensource.batch.statementprocessor.config;

import com.opensource.batch.statementprocessor.listener.JobExecutionCompletionListener;
import com.opensource.batch.statementprocessor.processor.IdentifyDuplicateTransactionDetailsProcessor;
import com.opensource.batch.statementprocessor.processor.TransactionDetailSkipper;
import com.opensource.batch.statementprocessor.processor.ValidateEndBalanceProcessor;
import com.opensource.batch.statementprocessor.vo.TransactionDetails;
import com.opensource.batch.statementprocessor.writer.ErrorReportWriter;
import com.opensource.batch.statementprocessor.writer.ValidReportWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.support.PassThroughItemProcessor;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableBatchProcessing
public class JobConfig {
    @Autowired
    public FileConfig fileConfig;

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;


    @Bean
    public MultiResourceItemReader<TransactionDetails> csvMultiResourceItemReader() {
        MultiResourceItemReader<TransactionDetails> resourceItemReader = new MultiResourceItemReader<TransactionDetails>();
        resourceItemReader.setResources(fileConfig.getCsvFiles());
        resourceItemReader.setDelegate(csvFileReader());

        return resourceItemReader;
    }
    @Bean
    public MultiResourceItemReader<TransactionDetails> xmlMultiResourceItemReader() {
        MultiResourceItemReader<TransactionDetails> resourceItemReader = new MultiResourceItemReader<TransactionDetails>();
        resourceItemReader.setResources(fileConfig.getXmlFiles());
        resourceItemReader.setDelegate(xmlFileItemReader());
        return resourceItemReader;
    }

    @Bean
    public FlatFileItemReader<TransactionDetails> csvFileReader() {
        return new FlatFileItemReaderBuilder<TransactionDetails>()
                .name("csvItemReader")
                .resource(new ClassPathResource(fileConfig.getCsvFile()))
                .delimited()
                .names(new String[]{"Reference", "AccountNumber", "Description", "Start Balance", "Mutation", "End Balance"})
                .linesToSkip(1)
                .fieldSetMapper(new BeanWrapperFieldSetMapper<TransactionDetails>() {{
                    setTargetType(TransactionDetails.class);
                }})
                .build();
    }


    @Bean
    public StaxEventItemReader<TransactionDetails> xmlFileItemReader() {
        StaxEventItemReader<TransactionDetails> xmlFileReader = new StaxEventItemReader<>();
        xmlFileReader.setResource(new ClassPathResource(fileConfig.getXmlFile()));
        xmlFileReader.setFragmentRootElementName("record");

        Jaxb2Marshaller studentMarshaller = new Jaxb2Marshaller();
        studentMarshaller.setClassesToBeBound(TransactionDetails.class);

        xmlFileReader.setUnmarshaller(studentMarshaller);
        return xmlFileReader;
    }


    @Bean
    public ItemProcessor<TransactionDetails, ? extends TransactionDetails> processor() {
        CompositeItemProcessor<TransactionDetails, TransactionDetails> compositeItemProcessor = new CompositeItemProcessor<>();
        List<ItemProcessor<TransactionDetails, TransactionDetails>> itemProcessors = new ArrayList<>();
        itemProcessors.add(new PassThroughItemProcessor());
        itemProcessors.add(new ValidateEndBalanceProcessor());
        itemProcessors.add(new IdentifyDuplicateTransactionDetailsProcessor());
        compositeItemProcessor.setDelegates(itemProcessors);
        return compositeItemProcessor;
    }

    @Bean
    public JobExecutionCompletionListener jobExecutionListenerSupport() {
        return new JobExecutionCompletionListener();
    }

    @Bean
    public CompositeItemWriter<TransactionDetails> writer() {
        CompositeItemWriter<TransactionDetails> compositeItemWriter = new CompositeItemWriter<>();
        List<ItemWriter<? super TransactionDetails>> itemWriters = new ArrayList<>();
        itemWriters.add(errorWriter());
        itemWriters.add(validWriter());
        compositeItemWriter.setDelegates(itemWriters);
        return compositeItemWriter;
    }

    @Bean
    public FlatFileItemWriter validWriter() {
        FlatFileItemWriter validReportWriter = new ValidReportWriter();
        validReportWriter.setLineAggregator(createLineAggregator());
        validReportWriter.setResource(new FileSystemResource(fileConfig.getValidTransactionReportFile()));
        validReportWriter.setShouldDeleteIfExists(Boolean.TRUE);
        validReportWriter.setAppendAllowed(Boolean.TRUE);
        return validReportWriter;
    }

    @Bean
    public FlatFileItemWriter errorWriter() {
        FlatFileItemWriter errorReportWriter = new ErrorReportWriter();
        errorReportWriter.setLineAggregator(createLineAggregator());
        errorReportWriter.setResource(new FileSystemResource(fileConfig.getErrorTransactionReportFile()));
        errorReportWriter.setShouldDeleteIfExists(Boolean.TRUE);
        errorReportWriter.setAppendAllowed(Boolean.TRUE);
        return errorReportWriter;
    }


    @Bean
    public Job readCSVFileJob(final Step step1) {
        return jobBuilderFactory.get("readCSVFileJob")
                .incrementer(new RunIdIncrementer())
                .listener(jobExecutionListenerSupport())
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public Job readXMLFileJob(final Step step2) {
        return jobBuilderFactory.get("readXMLFileJob")
                .incrementer(new RunIdIncrementer())
                .listener(jobExecutionListenerSupport())
                .flow(step2)
                .end()
                .build();
    }

    @Bean
    public SkipPolicy fileVerificationSkipper() {
        return new TransactionDetailSkipper();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .<TransactionDetails, TransactionDetails>chunk(10)
                .reader(csvMultiResourceItemReader())
                .faultTolerant()
                .skipPolicy(fileVerificationSkipper())
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean
    public Step step2() {
        return stepBuilderFactory.get("step2")
                .<TransactionDetails, TransactionDetails>chunk(10)
                .reader(xmlMultiResourceItemReader())
                .faultTolerant()
                .skipPolicy(fileVerificationSkipper())
                .processor(processor())
                .writer(writer())

                .build();
    }

    private LineAggregator<TransactionDetails> createLineAggregator() {
        DelimitedLineAggregator<TransactionDetails> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(",");
        FieldExtractor<TransactionDetails> fieldExtractor = createFieldExtractor();
        lineAggregator.setFieldExtractor(fieldExtractor);
        return lineAggregator;
    }

    private FieldExtractor<TransactionDetails> createFieldExtractor() {
        BeanWrapperFieldExtractor<TransactionDetails> extractor = new BeanWrapperFieldExtractor<>();
        extractor.setNames(new String[]{"accountNumber", "referenceId", "description", "startBalance", "endBalance", "mutation","errorDescription"});
        return extractor;
    }

}
