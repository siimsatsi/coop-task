package com.coop.loan;

import com.coop.loan.model.Loan;
import com.coop.loan.model.PaymentScheduleEntry;
import com.coop.loan.repository.PaymentScheduleEntryRepository;
import com.coop.loan.service.PaymentScheduleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentScheduleServiceTest {

    @Mock
    private PaymentScheduleEntryRepository scheduleRepository;

    @InjectMocks
    private PaymentScheduleService scheduleService;

    private Loan buildLoan(String amount, int termMonths, String margin, String baseRate) {
        return Loan.builder()
                .loanAmount(new BigDecimal(amount))
                .termMonths(termMonths)
                .interestMargin(new BigDecimal(margin))
                .baseInterestRate(new BigDecimal(baseRate))
                .build();
    }

    @Test
    void generate_correctNumberOfEntries() {
        Loan loan = buildLoan("10000.00", 12, "1.500", "3.840");

        scheduleService.generate(loan);

        ArgumentCaptor<List<PaymentScheduleEntry>> captor = ArgumentCaptor.captor();
        verify(scheduleRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).hasSize(12);
    }

    @Test
    void generate_paymentNumbersAreSequential() {
        Loan loan = buildLoan("10000.00", 6, "1.500", "3.840");

        scheduleService.generate(loan);

        ArgumentCaptor<List<PaymentScheduleEntry>> captor = ArgumentCaptor.captor();
        verify(scheduleRepository).saveAll(captor.capture());

        List<PaymentScheduleEntry> entries = captor.getValue();
        for (int i = 0; i < entries.size(); i++) {
            assertThat(entries.get(i).getPaymentNumber()).isEqualTo(i + 1);
        }
    }

    @Test
    void generate_lastEntryRemainingBalanceIsZero() {
        Loan loan = buildLoan("10000.00", 24, "1.500", "3.840");

        scheduleService.generate(loan);

        ArgumentCaptor<List<PaymentScheduleEntry>> captor = ArgumentCaptor.captor();
        verify(scheduleRepository).saveAll(captor.capture());

        List<PaymentScheduleEntry> entries = captor.getValue();
        assertThat(entries.getLast().getRemainingBalance())
                .isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void generate_totalPaymentEqualsPrincipalPlusInterest() {
        Loan loan = buildLoan("10000.00", 12, "1.500", "3.840");

        scheduleService.generate(loan);

        ArgumentCaptor<List<PaymentScheduleEntry>> captor = ArgumentCaptor.captor();
        verify(scheduleRepository).saveAll(captor.capture());

        captor.getValue().forEach(entry ->
                assertThat(entry.getTotalPayment())
                        .isEqualByComparingTo(entry.getPrincipal().add(entry.getInterest()))
        );
    }

    @Test
    void generate_zeroInterestRate_paymentEqualsPrincipalDividedByTerm() {
        Loan loan = buildLoan("12000.00", 12, "0.000", "0.000");

        scheduleService.generate(loan);

        ArgumentCaptor<List<PaymentScheduleEntry>> captor = ArgumentCaptor.captor();
        verify(scheduleRepository).saveAll(captor.capture());

        captor.getValue().forEach(entry ->
                assertThat(entry.getInterest()).isEqualByComparingTo(BigDecimal.ZERO)
        );
    }

    @Test
    void generate_singleMonthTerm_oneEntryWithFullPrincipal() {
        Loan loan = buildLoan("10000.00", 1, "1.500", "3.840");

        scheduleService.generate(loan);

        ArgumentCaptor<List<PaymentScheduleEntry>> captor = ArgumentCaptor.captor();
        verify(scheduleRepository).saveAll(captor.capture());

        List<PaymentScheduleEntry> entries = captor.getValue();
        assertThat(entries).hasSize(1);
        assertThat(entries.getFirst().getRemainingBalance()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void generate_largeLoanAmount_remainingBalanceIsZero() {
        Loan loan = buildLoan("9999999.99", 360, "1.500", "3.840");

        scheduleService.generate(loan);

        ArgumentCaptor<List<PaymentScheduleEntry>> captor = ArgumentCaptor.captor();
        verify(scheduleRepository).saveAll(captor.capture());

        assertThat(captor.getValue().getLast().getRemainingBalance())
                .isEqualByComparingTo(BigDecimal.ZERO);
    }
}
