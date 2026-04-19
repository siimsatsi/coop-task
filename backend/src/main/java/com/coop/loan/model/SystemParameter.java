package com.coop.loan.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "system_parameter")
@Getter
@Setter
public class SystemParameter {

    @Id
    @Column(name = "key", nullable = false, length = 50)
    private String key;

    @Column(name = "value", nullable = false, precision = 10, scale = 3)
    private BigDecimal value;

    @Column(name = "description", length = 255)
    private String description;
}
