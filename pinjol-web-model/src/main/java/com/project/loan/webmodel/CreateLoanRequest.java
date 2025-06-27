package com.project.loan.webmodel;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateLoanRequest {
    
    private BigDecimal amount;
} 