package com.coop.loan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.DecimalMin;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class LoanApplicationRequest {

    @NotBlank
    @Size(max = 32)
    private String firstName;

    @NotBlank
    @Size(max = 32)
    private String lastName;

    @NotBlank
    @Pattern(regexp = "[12345678]\\d{10}", message = "Invalid Estonian personal code format")
    private String personalCode;

    @NotNull
    @DecimalMin(value = "5000.00")
    private BigDecimal loanAmount;

    @NotNull
    @Min(6)
    @Max(360)
    private Integer termMonths;

    @NotNull
    @DecimalMin(value = "0.0")
    private BigDecimal interestMargin;

    @NotNull
    @DecimalMin(value = "0.0")
    private BigDecimal baseInterestRate;
}
