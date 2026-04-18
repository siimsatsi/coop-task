package com.coop.loan;

import com.coop.loan.model.Loan;
import com.coop.loan.model.LoanStatus;
import com.coop.loan.model.RejectionReason;
import com.coop.loan.properties.LoanProperties;
import com.coop.loan.repository.LoanRepository;
import com.coop.loan.service.LoanSubmissionService;
import com.coop.loan.service.PaymentScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.never;


@ExtendWith(MockitoExtension.class)
class LoanSubmissionServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private PaymentScheduleService paymentScheduleService;

    @Mock
    private LoanProperties loanProperties;

    @InjectMocks
    private LoanSubmissionService submissionService;

    private Loan validLoan;

    @BeforeEach
    void setUp() {
        validLoan = Loan.builder()
                .firstName("Siim")
                .lastName("Satsi")
                .personalCode("37605030299")
                .loanAmount(new BigDecimal("10000.00"))
                .termMonths(24)
                .interestMargin(new BigDecimal("1.500"))
                .baseInterestRate(new BigDecimal("3.840"))
                .build();
    }

    @Test
    void submit_validLoan_savesAndReturnsInReview() {
        when(loanProperties.getMaxCustomerAge()).thenReturn(70);
        when(loanRepository.existsByPersonalCodeAndStatusNotIn(anyString(), anyList())).thenReturn(false);
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));

        Loan result = submissionService.submit(validLoan);

        assertThat(result.getStatus()).isEqualTo(LoanStatus.IN_REVIEW);
        verify(paymentScheduleService).generate(any(Loan.class));
        verify(loanRepository, times(2)).save(any(Loan.class));
    }

    @Test
    void submit_customerTooOld_rejectsWithCorrectReason() {
        // age 75, should reject
        validLoan.setPersonalCode("35001011234");
        when(loanProperties.getMaxCustomerAge()).thenReturn(70);
        when(loanRepository.existsByPersonalCodeAndStatusNotIn(anyString(), anyList())).thenReturn(false);
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));

        Loan result = submissionService.submit(validLoan);

        assertThat(result.getStatus()).isEqualTo(LoanStatus.REJECTED);
        assertThat(result.getRejectionReason()).isEqualTo(RejectionReason.CUSTOMER_TOO_OLD);
        verify(paymentScheduleService, never()).generate(any());
    }

    @Test
    void submit_activeApplicationExists_throwsException() {
        when(loanRepository.existsByPersonalCodeAndStatusNotIn(anyString(), anyList())).thenReturn(true);

        assertThatThrownBy(() -> submissionService.submit(validLoan))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("active loan application");

        verify(loanRepository, never()).save(any());
    }

    @Test
    void submit_invalidPersonalCode_throwsException() {
        validLoan.setPersonalCode("12345678901");

        assertThatThrownBy(() -> submissionService.submit(validLoan))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("personal code");
    }

    @Test
    void submit_exactMinimumLoanAmount_succeeds() {
        validLoan.setLoanAmount(new BigDecimal("5000.00"));
        when(loanProperties.getMaxCustomerAge()).thenReturn(70);
        when(loanRepository.existsByPersonalCodeAndStatusNotIn(anyString(), anyList())).thenReturn(false);
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));

        Loan result = submissionService.submit(validLoan);

        assertThat(result.getStatus()).isEqualTo(LoanStatus.IN_REVIEW);
    }

    @Test
    void submit_exactMinimumTermMonths_succeeds() {
        validLoan.setTermMonths(6);
        when(loanProperties.getMaxCustomerAge()).thenReturn(70);
        when(loanRepository.existsByPersonalCodeAndStatusNotIn(anyString(), anyList())).thenReturn(false);
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));

        Loan result = submissionService.submit(validLoan);

        assertThat(result.getStatus()).isEqualTo(LoanStatus.IN_REVIEW);
    }

    @Test
    void submit_exactMaximumTermMonths_succeeds() {
        validLoan.setTermMonths(360);
        when(loanProperties.getMaxCustomerAge()).thenReturn(70);
        when(loanRepository.existsByPersonalCodeAndStatusNotIn(anyString(), anyList())).thenReturn(false);
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));

        Loan result = submissionService.submit(validLoan);

        assertThat(result.getStatus()).isEqualTo(LoanStatus.IN_REVIEW);
    }

    @Test
    void submit_zeroInterestMargin_succeeds() {
        validLoan.setInterestMargin(BigDecimal.ZERO);
        when(loanProperties.getMaxCustomerAge()).thenReturn(70);
        when(loanRepository.existsByPersonalCodeAndStatusNotIn(anyString(), anyList())).thenReturn(false);
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));

        Loan result = submissionService.submit(validLoan);

        assertThat(result.getStatus()).isEqualTo(LoanStatus.IN_REVIEW);
    }
}
