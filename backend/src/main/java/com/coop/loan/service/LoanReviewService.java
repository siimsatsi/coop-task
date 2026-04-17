package com.coop.loan.service;

import com.coop.loan.model.Loan;
import com.coop.loan.model.LoanStatus;
import com.coop.loan.model.RejectionReason;
import com.coop.loan.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoanReviewService {

    private final LoanRepository loanRepository;

    @Transactional
    public Loan approve(UUID loanId) {
        Loan loan = getInReview(loanId);
        loan.setStatus(LoanStatus.APPROVED);
        return loanRepository.save(loan);
    }

    @Transactional
    public Loan reject(UUID loanId, RejectionReason reason) {
        Loan loan = getInReview(loanId);
        loan.setStatus(LoanStatus.REJECTED);
        loan.setRejectionReason(reason);
        return loanRepository.save(loan);
    }

    private Loan getInReview(UUID loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found: " + loanId));
        if (loan.getStatus() != LoanStatus.IN_REVIEW) {
            throw new IllegalStateException("Loan is not in IN_REVIEW status: " + loan.getStatus());
        }
        return loan;
    }
}
