package com.coop.loan.repository;

import com.coop.loan.model.Loan;
import com.coop.loan.model.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoanRepository extends JpaRepository<Loan, UUID> {

    boolean existsByPersonalCodeAndStatusNotIn(String personalCode, List<LoanStatus> statuses);

    Optional<Loan> findByPersonalCodeAndStatusNotIn(String personalCode, List<LoanStatus> statuses);
}
