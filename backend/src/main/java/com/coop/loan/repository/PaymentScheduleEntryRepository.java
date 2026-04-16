package com.coop.loan.repository;

import com.coop.loan.model.PaymentScheduleEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentScheduleEntryRepository extends JpaRepository<PaymentScheduleEntry, UUID> {

    List<PaymentScheduleEntry> findByLoanIdOrderByPaymentNumberAsc(UUID loanId);

    void deleteByLoanId(UUID loanId);
}
