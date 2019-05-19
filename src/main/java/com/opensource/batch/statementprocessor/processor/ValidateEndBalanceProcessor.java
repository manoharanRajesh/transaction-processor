package com.opensource.batch.statementprocessor.processor;

import com.opensource.batch.statementprocessor.vo.TransactionDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static java.math.BigDecimal.ROUND_HALF_UP;

@Component
@Slf4j
public class ValidateEndBalanceProcessor implements ItemProcessor<TransactionDetails, TransactionDetails> {

    private static final int SCALE = 2;

    @Override
    public TransactionDetails process(final TransactionDetails transactionDetails) {
        if (transactionDetails.getEndBalance() == null || transactionDetails.getStartBalance() == null || transactionDetails.getMutation() == null) {
            log.error("Expect start,mutation and end value to be valid. check transaction {}", transactionDetails.getReferenceId());
            transactionDetails.setWrongEndBalance(Boolean.TRUE);
            return transactionDetails;
        }

        BigDecimal calculatedEndBalance = calculateEndBalance(transactionDetails.getStartBalance(), transactionDetails.getMutation());
        transactionDetails.setWrongEndBalance(!isEqual(calculatedEndBalance, transactionDetails.getEndBalance()));
        return transactionDetails;
    }

    private boolean isEqual(BigDecimal expectedEndBalance, BigDecimal actualEndBalance) {
        return expectedEndBalance.equals(actualEndBalance.setScale(SCALE, ROUND_HALF_UP));
    }


    private BigDecimal calculateEndBalance(BigDecimal startBalance, BigDecimal mutation) {
        return startBalance.add(mutation).setScale(SCALE, ROUND_HALF_UP);
    }

}
