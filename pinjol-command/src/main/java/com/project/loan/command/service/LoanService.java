package com.project.loan.command.service;

import com.project.loan.command.helper.TimeHelper;
import com.project.loan.configuration.LoanProperties;
import com.project.loan.entity.Loan;
import com.project.loan.entity.PaymentSchedule;
import com.project.loan.repository.LoanRepository;
import com.project.loan.repository.PaymentScheduleRepository;
import com.project.loan.webmodel.CreateLoanRequest;
import com.project.loan.webmodel.OutstandingLoanResponse;
import com.project.loan.webmodel.DelinquentStatusResponse;
import com.project.loan.webmodel.MakePaymentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional
public class LoanService {

  @Autowired
  private final LoanRepository loanRepository;

  @Autowired
  private final PaymentScheduleRepository paymentScheduleRepository;

  @Autowired
  private TimeHelper timeHelper;

  @Autowired
  private LoanProperties loanProperties;

  public Loan createLoan(CreateLoanRequest request) {

    // Create loan
    Loan loan = new Loan();
    loan.setTotalAmount(request.getAmount());
    loan.setPrincipal(request.getAmount()); // Set initial principal to total amount
    loan.setInterestRate(loanProperties.getInterestRate());
    loan.setTerm(loanProperties.getTerm());
    loan.setStatus(Loan.LoanStatus.APPROVED);
    loan.setFirstPaymentDate(timeHelper.getNDaysAhead(7)); // Set first payment date to 7 days from now

    // Calculate weekly payment amount
    BigDecimal weeklyPaymentAmount = request.getAmount()
        .multiply(loanProperties.getInterestRate())
        .add(request.getAmount())
        .divide(BigDecimal.valueOf(loanProperties.getTerm()), RoundingMode.HALF_UP);

    BigDecimal weeklyInterestAmount = request.getAmount()
        .multiply(loanProperties.getInterestRate())
        .divide(BigDecimal.valueOf(loanProperties.getTerm()), RoundingMode.HALF_UP);

    loan.setWeeklyPaymentAmount(weeklyPaymentAmount);

    // Save loan first
    loan = loanRepository.save(loan);

    // Generate payment schedule
    List<PaymentSchedule> paymentSchedules =
        generatePaymentSchedule(loan, weeklyPaymentAmount, weeklyInterestAmount);
    paymentScheduleRepository.saveAll(paymentSchedules);

    return loan;
  }

  private List<PaymentSchedule> generatePaymentSchedule(Loan loan,
      BigDecimal weeklyPayment,
      BigDecimal weeklyInterest) {
    List<PaymentSchedule> schedules = new ArrayList<>();
    BigDecimal remainingPrincipal = loan.getTotalAmount();
    long dueDate = loan.getFirstPaymentDate();

    for (int i = 1; i <= loan.getTerm(); i++) {
      PaymentSchedule schedule = new PaymentSchedule();
      schedule.setLoan(loan);
      schedule.setPaymentNumber(i);
      schedule.setDueDate(dueDate);

      // Calculate principal for this payment
      BigDecimal principalAmount = weeklyPayment.subtract(weeklyInterest);

      // Ensure we don't overpay principal in the last payment
      if (i == loan.getTerm()) {
        principalAmount = remainingPrincipal;
      }

      schedule.setInterestAmount(weeklyInterest);
      schedule.setPrincipalAmount(principalAmount);
      schedule.setTotalAmount(weeklyPayment);
      schedule.setStatus(PaymentSchedule.PaymentStatus.PENDING);

      schedules.add(schedule);

      // Update remaining principal and due date for next iteration
      remainingPrincipal = remainingPrincipal.subtract(principalAmount);
      dueDate = timeHelper.getNDaysAhead(dueDate, 7); // Set next payment due date 7 days later
    }

    return schedules;
  }

  public OutstandingLoanResponse getOutstandingLoan(String loanId) {
    Loan loan = loanRepository.findById(loanId)
        .orElseThrow(() -> new RuntimeException("Loan not found with id: " + loanId));

    return new OutstandingLoanResponse(loan.getId(),
        loan.getPrincipal(),
        loan.getTotalAmount(),
        loan.getInterestRate(),
        loan.getTerm(),
        loan.getStatus().name());
  }

  public DelinquentStatusResponse checkDelinquentStatus(String loanId) {

    //Get delinquent limit based on configured days(e.g., 14 days ago)
    long delinquentLimit = timeHelper.getNDaysAhead(loanProperties.getDelinquentDaysLimit());
    PaymentSchedule pendingPayment =
        paymentScheduleRepository.findFirstByLoanIdAndDueDateBeforeAndStatus(loanId,
            delinquentLimit,
            PaymentSchedule.PaymentStatus.PENDING);

    if (Objects.nonNull(pendingPayment)) {
      long overDue = timeHelper.getCurrentEpoch() - pendingPayment.getDueDate();
      long day = TimeUnit.DAYS.toMillis(1);

      long overDueInDays = overDue/day;
      return new DelinquentStatusResponse(loanId, true, "Delinquent payment found", overDueInDays);
    }

    return new DelinquentStatusResponse(loanId, false, "No delinquent payments", 0L);
  }

  public PaymentSchedule makePayment(String loanId, MakePaymentRequest request) {
    Loan loan = loanRepository.findById(loanId)
        .orElseThrow(() -> new RuntimeException("Loan not found with id: " + loanId));

    PaymentSchedule pendingPayment =
        paymentScheduleRepository.findFirstByLoanIdAndStatusOrderByPaymentNumberAsc(loanId,
            PaymentSchedule.PaymentStatus.PENDING);

    // Check if payment amount is sufficient
    if (request.getPaymentAmount().compareTo(pendingPayment.getTotalAmount()) != 0) {
      throw new RuntimeException(
          "Payment amount is not equal. Required: " + pendingPayment.getTotalAmount());
    }

    // Mark payment as paid
    pendingPayment.setStatus(PaymentSchedule.PaymentStatus.PAID);
    pendingPayment.setPaidDate(timeHelper.getCurrentEpoch());

    // Reduce principal in loan
    loan.setPrincipal(loan.getPrincipal().subtract(pendingPayment.getPrincipalAmount()));

    // Save both entities
    paymentScheduleRepository.save(pendingPayment);
    loanRepository.save(loan);

    return pendingPayment;
  }
} 