package com.opensource.batch.statementprocessor.writer;

import com.opensource.batch.statementprocessor.vo.TransactionDetails;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.item.file.FlatFileItemWriter;

import java.util.List;
import java.util.stream.Collectors;

public class ErrorReportWriter extends FlatFileItemWriter<TransactionDetails> {

    @Override
    public String doWrite(final List<? extends TransactionDetails> items) {
        return items.stream()
                .filter(item -> item.isDuplicate() || item.isWrongEndBalance())
                .map(item -> {
                    StringBuilder errorDescription = new StringBuilder();
                    errorDescription.append(StringUtils.defaultIfEmpty(item.getErrorDescription(), ""));
                    if (item.isDuplicate()) {
                        errorDescription.append("Duplication Transaction.");
                    }
                    if (item.isWrongEndBalance()) {
                        errorDescription.append("Wrong end balance.");
                    }
                    item.setErrorDescription(errorDescription.toString());
                    return item;
                })
                .map(item -> this.lineAggregator.aggregate(item) + this.lineSeparator)
                .collect(Collectors.joining());
    }
}
