package com.coop.loan.service;

import com.coop.loan.model.Loan;
import com.coop.loan.model.LoanStatus;
import com.coop.loan.model.RejectionReason;
import com.coop.loan.properties.LoanProperties;
import com.coop.loan.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanSubmissionService {

    private final LoanRepository loanRepository;
    private final PaymentScheduleService paymentScheduleService;
    private final SystemParameterService systemParameterService;

    @Transactional
    public Loan submit(Loan loan) {
        if (hasActiveApplication(loan.getPersonalCode())) {
            throw new IllegalStateException("Customer already has an active loan application");
        }

        if (!SocialSecurityNumberService.isValid(loan.getPersonalCode())) {
            throw new IllegalArgumentException("Invalid Estonian personal code");
        }

        int age = SocialSecurityNumberService.extractAge(loan.getPersonalCode());

        if (age > systemParameterService.getMaxCustomerAge()) {
            loan.setStatus(LoanStatus.REJECTED);
            loan.setRejectionReason(RejectionReason.CUSTOMER_TOO_OLD);
            return loanRepository.save(loan);
        }

        loan.setStatus(LoanStatus.STARTED);
        Loan saved = loanRepository.save(loan);

        paymentScheduleService.generate(saved);

        saved.setStatus(LoanStatus.IN_REVIEW);
        return loanRepository.save(saved);
    }

    private boolean hasActiveApplication(String personalCode) {
        return loanRepository.existsByPersonalCodeAndStatusNotIn(
                personalCode,
                List.of(LoanStatus.APPROVED, LoanStatus.REJECTED)
        );
    }
}
