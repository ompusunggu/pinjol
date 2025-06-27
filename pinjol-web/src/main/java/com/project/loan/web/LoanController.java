package com.project.loan.web;

import com.project.loan.webmodel.CreateLoanRequest;
import com.project.loan.webmodel.OutstandingLoanResponse;
import com.project.loan.webmodel.DelinquentStatusResponse;
import com.project.loan.webmodel.MakePaymentRequest;
import com.project.loan.entity.Loan;
import com.project.loan.entity.PaymentSchedule;
import com.project.loan.command.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    @Autowired
    private final LoanService loanService;

    @PostMapping
    public ResponseEntity<Loan> createLoan(@Valid @RequestBody CreateLoanRequest request) {
        Loan loan = loanService.createLoan(request);
        return ResponseEntity.ok(loan);
    }
    
    @GetMapping("/{loanId}/outstanding")
    public ResponseEntity<OutstandingLoanResponse> getOutstandingLoan(@PathVariable String loanId) {
        OutstandingLoanResponse response = loanService.getOutstandingLoan(loanId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{loanId}/delinquent")
    public ResponseEntity<DelinquentStatusResponse> checkDelinquentStatus(@PathVariable String loanId) {
        DelinquentStatusResponse response = loanService.checkDelinquentStatus(loanId);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{loanId}/payment")
    public ResponseEntity<PaymentSchedule> makePayment(@PathVariable String loanId, 
                                                      @Valid @RequestBody MakePaymentRequest request) {
        PaymentSchedule payment = loanService.makePayment(loanId, request);
        return ResponseEntity.ok(payment);
    }
} 