package com.opensource.batch.statementprocessor.writer;

import com.opensource.batch.statementprocessor.vo.TransactionDetails;
import org.springframework.batch.item.file.FlatFileItemWriter;

import java.util.List;
import java.util.stream.Collectors;

public class ErrorReportWriter extends FlatFileItemWriter<TransactionDetails> {

    @Override
    public String doWrite(final List<? extends TransactionDetails> items) {
        return items.stream()
                .filter(item -> item.isDuplicate() || item.isWrongEndBalance())
                .map(item -> this.lineAggregator.aggregate(item) + this.lineSeparator)
                .map(item -> {
                    System.out.println(item);
                    return item;
                })
                .collect(Collectors.joining());
    }
}
