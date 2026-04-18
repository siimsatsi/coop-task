package com.coop.loan.dto;

import com.coop.loan.model.RejectionReason;

import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanRejectRequest {

    @NotNull
    private RejectionReason reason;
}
