package com.coop.loan.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class PaymentScheduleEntryResponse {

    private UUID id;
    private int paymentNumber;
    private LocalDate paymentDate;
    private BigDecimal principal;
    private BigDecimal interest;
    private BigDecimal totalPayment;
    private BigDecimal remainingBalance;
}
