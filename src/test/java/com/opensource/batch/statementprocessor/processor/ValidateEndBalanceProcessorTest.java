package com.opensource.batch.statementprocessor.processor;

import com.opensource.batch.statementprocessor.vo.TransactionDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertSame;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@DisplayName("Validate End balance : Start balance + mutation = end total")
class ValidateEndBalanceProcessorTest {
    @InjectMocks
    ValidateEndBalanceProcessor processor;

    @ParameterizedTest(name = "{0} + {1} = {2} expected to be {4}")
    @CsvSource({
            "0,    1,   1, false, VALID_END_BALANCE",
            "0.09,    0.001,   0.09, false, VALID_END_BALANCE",
            "0,    -1,   -1, false, VALID_END_BALANCE",
            "1,  -100, 105, true,INVALID_END_BALANCE",
            "0.1,    -1,   -0.9, false, VALID_END_BALANCE",
            "50.1,  .05, 50, true,INVALID_END_BALANCE",
            ",  .05, 50, true,INVALID_END_BALANCE",
            "50.1,  .05, , true,INVALID_END_BALANCE",
            "50.1,  , 50, true,INVALID_END_BALANCE"
    })
    void test_cal_eq_end_balance(BigDecimal startBal, BigDecimal mutation, BigDecimal EndBal, boolean isaValidTransaction, String expected) {
        TransactionDetails td = TransactionDetails.builder()
                .accountNumber("NL93ABNA0585619023")
                .referenceId("167875")
                .description("credit")
                .startBalance(startBal)
                .mutation(mutation)
                .endBalance(EndBal).build();

        TransactionDetails actual = processor.process(td);

        assertSame(isaValidTransaction, actual.isWrongEndBalance(), "End balance validation is wrong.");
    }

}
