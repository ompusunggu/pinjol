package com.project.loan.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

@Data
@ConfigurationProperties("pinjol.loan")
public class LoanProperties {

  private BigDecimal interestRate;

  private int term;

  private int delinquentDaysLimit;
} 