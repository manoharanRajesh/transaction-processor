package com.opensource.batch.statementprocessor.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TransactionDetailSkipper implements SkipPolicy {

    @Override
    public boolean shouldSkip(Throwable t, int skipCount) throws SkipLimitExceededException {
        if (t instanceof FlatFileParseException && skipCount <= 5) {
            FlatFileParseException flatFileParseException = (FlatFileParseException) t;
            log.error("An error occurred while processing the {} line of the file. " +
                            "Below was the faulty input. \n {} \n",
                    flatFileParseException.getLineNumber(), flatFileParseException.getInput());
            return true;
        }
        return false;
    }
}
