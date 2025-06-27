package com.project.loan.webmodel;

import lombok.Data;

@Data
public class DelinquentStatusResponse {
    
    private String loanId;
    private boolean isDelinquent;
    private String reason;
    private Long daysOverdue;
    
    public DelinquentStatusResponse(String loanId, boolean isDelinquent, String reason, Long daysOverdue) {
        this.loanId = loanId;
        this.isDelinquent = isDelinquent;
        this.reason = reason;
        this.daysOverdue = daysOverdue;
    }
} 