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
@DisplayName("Test for duplicate transaction details")
class IdentifyDuplicateTransactionDetailsProcessorTest {

    @InjectMocks
    IdentifyDuplicateTransactionDetailsProcessor duplicateTransactionDetailsProcessor;

    @Test
    @DisplayName("Give a valid set of transaction record with one duplicate entry.")
    void test_duplicate_transactions() {
        List<TransactionDetails> lst = Lists.newArrayList(
                TransactionDetails.builder().referenceId("1").build(),
                TransactionDetails.builder().referenceId("2").build(),
                TransactionDetails.builder().referenceId("3").build(),
                TransactionDetails.builder().referenceId(" ").build(),
                TransactionDetails.builder().referenceId("a").build(),
                TransactionDetails.builder().referenceId("A").build(),
                TransactionDetails.builder().referenceId("$").build(),
                TransactionDetails.builder().referenceId("$").build(),
                TransactionDetails.builder().referenceId(" ").build(),
                TransactionDetails.builder().referenceId("1").build(),
                TransactionDetails.builder().referenceId(null).build(),
                TransactionDetails.builder().referenceId("7").build()
        );

        lst.stream().forEach(t ->
                duplicateTransactionDetailsProcessor.process(t)
        );

        assertAll("Check last record is Duplication and other are not duplicate",
                () -> assertFalse(lst.get(0).isDuplicate(), "Expect duplicate indicate should be false for " + lst.get(0)),
                () -> assertFalse(lst.get(1).isDuplicate(), "Expect duplicate indicate should be false for " + lst.get(1)),
                () -> assertFalse(lst.get(2).isDuplicate(), "Expect duplicate indicate should be false for " + lst.get(2)),
                () -> assertFalse(lst.get(3).isDuplicate(), "Expect duplicate indicate should be false for " + lst.get(3)),
                () -> assertFalse(lst.get(4).isDuplicate(), "Expect duplicate indicate should be false for " + lst.get(4)),
                () -> assertFalse(lst.get(5).isDuplicate(), "Expect duplicate indicate should be false for " + lst.get(5)),
                () -> assertFalse(lst.get(6).isDuplicate(), "Expect duplicate indicate should be false for " + lst.get(6)),
                () -> assertTrue(lst.get(7).isDuplicate(), "Expect duplicate indicate should be true for " + lst.get(7)),
                () -> assertTrue(lst.get(8).isDuplicate(), "Expect duplicate indicate should be true for " + lst.get(8)),
                () -> assertTrue(lst.get(9).isDuplicate(), "Expect duplicate indicate should be true for " + lst.get(9)),
                () -> assertFalse(lst.get(10).isDuplicate(), "Expect duplicate indicate should be false for " + lst.get(10)),
                () -> assertFalse(lst.get(11).isDuplicate(), "Expect duplicate indicate should be false for " + lst.get(11))
        );
    }

}
