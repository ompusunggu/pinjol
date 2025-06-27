package com.project.loan.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Loan {
    
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String id;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "interest_rate", nullable = false)
    private BigDecimal interestRate;
    
    private Integer term; 
    
    @Column(name = "weekly_payment_amount")
    private BigDecimal weeklyPaymentAmount; 
    
    @Column(name = "principal", nullable = false)
    private BigDecimal principal;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "loan_status")
    private LoanStatus status = LoanStatus.PENDING;
    
    @Column(name = "first_payment_date")
    private Long firstPaymentDate;
    
    @Column(name = "created_at")
    private Long createdAt;
    
    @Column(name = "updated_at")
    private Long updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        updatedAt = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        if (principal == null) {
            principal = totalAmount;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
    
    public enum LoanStatus {
        PENDING, APPROVED, DISBURSED, ACTIVE, COMPLETED, DEFAULTED, CANCELLED
    }
} 