package com.coop.loan.dto;

import com.coop.loan.model.LoanStatus;
import com.coop.loan.model.RejectionReason;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class LoanApplicationResponse {

    private UUID id;
    private String firstName;
    private String lastName;
    private String personalCode;
    private BigDecimal loanAmount;
    private int termMonths;
    private BigDecimal interestMargin;
    private BigDecimal baseInterestRate;
    private LoanStatus status;
    private RejectionReason rejectionReason;
    private LocalDateTime createdAt;
    private List<PaymentScheduleEntryResponse> paymentSchedule;
}
