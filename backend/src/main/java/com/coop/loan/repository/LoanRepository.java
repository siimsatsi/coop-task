package com.coop.loan.repository;

import com.coop.loan.model.Loan;
import com.coop.loan.model.LoanStatus;
import io.micrometer.common.lang.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoanRepository extends JpaRepository<Loan, UUID> {

    List<Loan> findByStatus(@NonNull LoanStatus status);

    boolean existsByPersonalCodeAndStatusNotIn(String personalCode, List<LoanStatus> statuses);

    Optional<Loan> findByPersonalCodeAndStatusNotIn(String personalCode, List<LoanStatus> statuses);
}
