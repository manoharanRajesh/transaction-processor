package com.opensource.batch.statementprocessor.processor;

import com.opensource.batch.statementprocessor.vo.TransactionDetails;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@DisplayName("Test for mutation of duplicate Indicator for different transaction details")
class IdentifyDuplicateTransactionDetailsProcessorTest {

    @InjectMocks
    IdentifyDuplicateTransactionDetailsProcessor duplicateTransactionDetailsProcessor;


    @Test
    @DisplayName("Non duplicate transactions")
    void test_non_duplicate_transactions() {
        List<TransactionDetails> lst = Lists.newArrayList(
                TransactionDetails.builder().referenceId("1").build(),
                TransactionDetails.builder().referenceId("1asdasd").build(),
                TransactionDetails.builder().referenceId("a").build(),
                TransactionDetails.builder().referenceId("A").build(),
                TransactionDetails.builder().referenceId("$").build()
        );

        lst.forEach(t ->
                duplicateTransactionDetailsProcessor.process(t)
        );

        lst.forEach(
                t -> {
                    assertFalse(t.isDuplicate(), t.getReferenceId()+ " duplication indicator have to be false.");
                }
        );
    }


    @Test
    @DisplayName("duplicate transactions")
    void test_duplicate_transactions() {
        List<TransactionDetails> lst = Lists.newArrayList(
                TransactionDetails.builder().referenceId("1").build(),
                TransactionDetails.builder().referenceId("1").build(),
                TransactionDetails.builder().referenceId("$wqeq").build(),
                TransactionDetails.builder().referenceId("$wqeq").build(),
                TransactionDetails.builder().referenceId(" ").build(),
                TransactionDetails.builder().referenceId(null).build()
        );

        lst.forEach(t ->
                duplicateTransactionDetailsProcessor.process(t)
        );

        assertAll("Check for duplicate records indicator",
                () -> assertTrue(lst.get(1).isDuplicate(), "Expect duplicate indicate to be true for " + lst.get(1)),
                () -> assertTrue(lst.get(3).isDuplicate(), "Expect duplicate indicate to be true for " + lst.get(3)),
                () -> assertTrue(lst.get(4).isDuplicate(), "Expect duplicate indicate to be true for " + lst.get(4)),
                () -> assertTrue(lst.get(5).isDuplicate(), "Expect duplicate indicate to be true for " + lst.get(5))
        );
    }

}
