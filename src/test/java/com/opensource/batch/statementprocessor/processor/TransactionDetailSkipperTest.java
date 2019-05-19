package com.opensource.batch.statementprocessor.processor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@DisplayName("Handle exception and max limit of exception.")
class TransactionDetailSkipperTest {

    @InjectMocks
    TransactionDetailSkipper transactionDetailSkipper;

    @Test
    @DisplayName("reached max limit")
    void test_max_limit() {
        assertFalse(transactionDetailSkipper.shouldSkip(new FlatFileParseException("", ""), 6));
    }

    @Test
    @DisplayName("ignore the error")
    void test_with_in_threshold() {
        assertTrue(transactionDetailSkipper.shouldSkip(new FlatFileParseException("", ""), 1));
    }

    @Test
    @DisplayName("unknown error")
    void test_unknown_error() {
        assertFalse(transactionDetailSkipper.shouldSkip(new NullPointerException(""), 1));
    }
}
