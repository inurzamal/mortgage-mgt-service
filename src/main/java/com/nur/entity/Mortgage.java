package com.nur.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
public class Mortgage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String mortgageNumber;

    private BigDecimal amount;

    private BigDecimal interestRate;

    private LocalDate startDate;

    private LocalDate endDate;

    private Long customerId;
}
