package com.coop.loan.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.loan")
public class LoanProperties {

    private int maxCustomerAge;
    private BigDecimal minAmount;
    private int minTermMonths;
    private int maxTermMonths;
}
