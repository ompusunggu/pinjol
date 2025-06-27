package com.project.loan.webmodel;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class OutstandingLoanResponse {
    
    private String loanId;
    private BigDecimal outstandingPrincipal;
    private BigDecimal totalAmount;
    private BigDecimal interestRate;
    private Integer term;
    private String status;
} 