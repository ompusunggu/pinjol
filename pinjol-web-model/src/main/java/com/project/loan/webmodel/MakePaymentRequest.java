package com.project.loan.webmodel;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MakePaymentRequest {
    
    @NotNull
    private BigDecimal paymentAmount;
} 