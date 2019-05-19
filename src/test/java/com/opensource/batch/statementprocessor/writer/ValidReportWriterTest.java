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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@DisplayName("Unit test for report writer of successful transactions")
class ValidReportWriterTest {
    @Mock
    LineAggregator lineAggregatorMock;
    @InjectMocks
    ValidReportWriter writer;

    @Test
    @DisplayName("When all transaction are valid then all transaction should be in the report.")
    void test_all_valid_transaction() {
        when(this.lineAggregatorMock.aggregate(any())).thenReturn("Record");
        List<TransactionDetails> validTransactions = Lists.newArrayList(
                TransactionDetails.builder().duplicate(false).wrongEndBalance(false).referenceId("1").build(),
                TransactionDetails.builder().duplicate(false).wrongEndBalance(false).referenceId("2").build()
        );

        String actual = writer.doWrite(validTransactions);

        //Check it if it is call for invalid record alone.
        verify(this.lineAggregatorMock, times(2)).aggregate(any());
    }


    @Test
    @DisplayName("When all transaction has errors then report is empty.")
    void test_all_error_transaction() {

        List<TransactionDetails> validTransactions = Lists.newArrayList(
                TransactionDetails.builder().duplicate(true).wrongEndBalance(false).referenceId("1").build(),
                TransactionDetails.builder().duplicate(false).wrongEndBalance(true).referenceId("2").build(),
                TransactionDetails.builder().duplicate(true).wrongEndBalance(true).referenceId("3").build()
        );

        assertTrue(StringUtils.isBlank(writer.doWrite(validTransactions)));

    }

    @Test
    @DisplayName("When both success and error transaction are present then only success is written filtered. ")
    void test_both_transaction() {
        when(this.lineAggregatorMock.aggregate(any())).thenReturn("Record");
        List<TransactionDetails> validTransactions = Lists.newArrayList(
                TransactionDetails.builder().duplicate(true).wrongEndBalance(false).referenceId("1").build(),
                TransactionDetails.builder().duplicate(false).wrongEndBalance(true).referenceId("2").build(),
                TransactionDetails.builder().duplicate(false).wrongEndBalance(false).referenceId("3").build(),
                TransactionDetails.builder().duplicate(true).wrongEndBalance(true).referenceId("4").build()
        );

        assertTrue(StringUtils.isNotBlank(writer.doWrite(validTransactions)));

        //Check it if it is call for invalid record alone.
        verify(this.lineAggregatorMock, times(1)).aggregate(any());
    }
}
