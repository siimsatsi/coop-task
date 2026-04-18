package com.coop.loan;

import com.coop.loan.model.Loan;
import com.coop.loan.model.LoanStatus;
import com.coop.loan.model.RejectionReason;
import com.coop.loan.repository.LoanRepository;
import com.coop.loan.service.LoanReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LoanReviewServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private LoanReviewService reviewService;

    private Loan inReviewLoan;
    private UUID loanId;

    @BeforeEach
    void setUp() {
        loanId = UUID.randomUUID();
        inReviewLoan = Loan.builder()
                .id(loanId)
                .status(LoanStatus.IN_REVIEW)
                .build();
    }

    @Test
    void approve_inReviewLoan_setsStatusApproved() {
        when(loanRepository.findById(loanId)).thenReturn(Optional.of(inReviewLoan));
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));

        Loan result = reviewService.approve(loanId);

        assertThat(result.getStatus()).isEqualTo(LoanStatus.APPROVED);
        verify(loanRepository).save(inReviewLoan);
    }

    @Test
    void reject_inReviewLoan_setsStatusRejectedWithReason() {
        when(loanRepository.findById(loanId)).thenReturn(Optional.of(inReviewLoan));
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));

        Loan result = reviewService.reject(loanId, RejectionReason.MANUAL_REJECTION);

        assertThat(result.getStatus()).isEqualTo(LoanStatus.REJECTED);
        assertThat(result.getRejectionReason()).isEqualTo(RejectionReason.MANUAL_REJECTION);
    }

    @Test
    void approve_loanNotFound_throwsException() {
        when(loanRepository.findById(loanId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.approve(loanId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Loan not found");
    }

    @Test
    void reject_loanNotFound_throwsException() {
        when(loanRepository.findById(loanId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.reject(loanId, RejectionReason.MANUAL_REJECTION))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Loan not found");
    }

    @Test
    void approve_alreadyApprovedLoan_throwsException() {
        inReviewLoan.setStatus(LoanStatus.APPROVED);
        when(loanRepository.findById(loanId)).thenReturn(Optional.of(inReviewLoan));

        assertThatThrownBy(() -> reviewService.approve(loanId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("IN_REVIEW");
    }

    @Test
    void reject_alreadyRejectedLoan_throwsException() {
        inReviewLoan.setStatus(LoanStatus.REJECTED);
        when(loanRepository.findById(loanId)).thenReturn(Optional.of(inReviewLoan));

        assertThatThrownBy(() -> reviewService.reject(loanId, RejectionReason.MANUAL_REJECTION))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("IN_REVIEW");
    }

    @Test
    void getById_existingLoan_returnsLoan() {
        when(loanRepository.findById(loanId)).thenReturn(Optional.of(inReviewLoan));

        Loan result = reviewService.getById(loanId);

        assertThat(result).isEqualTo(inReviewLoan);
    }

    @Test
    void getById_nonExistentLoan_throwsException() {
        when(loanRepository.findById(loanId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.getById(loanId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Loan not found");
    }
}
