package com.opensource.batch.statementprocessor.processor;

import com.opensource.batch.statementprocessor.vo.TransactionDetails;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.item.ItemProcessor;

import java.util.HashSet;
import java.util.Set;

@Slf4j
public class IdentifyDuplicateTransactionDetailsProcessor implements ItemProcessor<TransactionDetails, TransactionDetails> {

    private final Set<TransactionDetails> seenTransactionDetails = new HashSet<>();

    @Override
    public TransactionDetails process(final TransactionDetails transactionDetails) {
        log.debug("Converting ({})", transactionDetails);
        if (StringUtils.isBlank(transactionDetails.getReferenceId())) {
            log.error("In valid reference ID {}", transactionDetails);
        }
        if (isRecordExists(transactionDetails)) {
            transactionDetails.setDuplicate(true);
        } else {
            seenTransactionDetails.add(transactionDetails);
        }
        return transactionDetails;
    }

    private boolean isRecordExists(TransactionDetails transactionDetails) {
        return seenTransactionDetails.stream()
                .anyMatch(seenTransactionDetail -> StringUtils.equals(transactionDetails.getReferenceId(), seenTransactionDetail.getReferenceId()));
    }
}
