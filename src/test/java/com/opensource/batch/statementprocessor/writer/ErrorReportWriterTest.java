package com.opensource.batch.statementprocessor.writer;

import com.opensource.batch.statementprocessor.vo.TransactionDetails;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.batch.item.support.AbstractFileItemWriter.DEFAULT_LINE_SEPARATOR;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@DisplayName("Unit test for error report writer")
class ErrorReportWriterTest {
    @Mock
    LineAggregator lineAggregatorMock;
    @InjectMocks
    ErrorReportWriter writer;

    @Test
    @DisplayName("When all transaction are valid then output should be empty")
    void test_write_only_errors() {
        List<TransactionDetails> validTransactions = Lists.newArrayList(
                TransactionDetails.builder().duplicate(false).wrongEndBalance(false).referenceId("1").build(),
                TransactionDetails.builder().duplicate(false).wrongEndBalance(false).referenceId("2").build()
        );

        String actual = writer.doWrite(validTransactions);

        assertTrue(StringUtils.isBlank(actual));
    }

    @Test
    @DisplayName("When transaction has duplications then it should be in the output.")
    void test_duplicate_transaction() {
        when(this.lineAggregatorMock.aggregate(any())).thenReturn("Record");
        List<TransactionDetails> validTransactions = Lists.newArrayList(
                TransactionDetails.builder().duplicate(true).wrongEndBalance(false).referenceId("1").build(),
                TransactionDetails.builder().duplicate(false).wrongEndBalance(false).referenceId("2").build()
        );


        String actual = writer.doWrite(validTransactions);

        //Check it if it is call for invalid record alone.
        verify(this.lineAggregatorMock, times(1)).aggregate(any());
        assertEquals(actual, "Record" + DEFAULT_LINE_SEPARATOR);
    }

    @Test
    @DisplayName("When transaction has wrong end balance then it should be in the output.")
    void test_wrong_endbalance_transaction() {
        when(this.lineAggregatorMock.aggregate(any())).thenReturn("Record");
        List<TransactionDetails> validTransactions = Lists.newArrayList(
                TransactionDetails.builder().duplicate(false).wrongEndBalance(true).referenceId("1").build(),
                TransactionDetails.builder().duplicate(false).wrongEndBalance(false).referenceId("2").build()
        );

        String actual = writer.doWrite(validTransactions);

        //Check it if it is call for invalid record alone.
        verify(this.lineAggregatorMock, times(1)).aggregate(any());
        assertEquals(actual, "Record" + DEFAULT_LINE_SEPARATOR);
    }


    @Test
    @DisplayName("When all possible transaction error")
    void test_wrong_both_transaction() {
        when(this.lineAggregatorMock.aggregate(any())).thenReturn("Record");
        List<TransactionDetails> validTransactions = Lists.newArrayList(
                TransactionDetails.builder().duplicate(false).wrongEndBalance(true).referenceId("1").build(),
                TransactionDetails.builder().duplicate(true).wrongEndBalance(false).referenceId("2").build(),
                TransactionDetails.builder().duplicate(true).wrongEndBalance(true).referenceId("3").build()
        );

        writer.doWrite(validTransactions);

        //Check it if it is call for invalid record alone.
        verify(this.lineAggregatorMock, times(3)).aggregate(any());
    }
}
