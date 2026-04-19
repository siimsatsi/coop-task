package com.coop.loan.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SystemParameterResponse {
    private String key;
    private BigDecimal value;
    private String description;
}
