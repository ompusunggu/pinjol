package com.project.loan.repository;

import com.project.loan.entity.PaymentSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentScheduleRepository extends JpaRepository<PaymentSchedule, String> {
    
    List<PaymentSchedule> findByLoanIdOrderByPaymentNumberAsc(String loanId);
    
    List<PaymentSchedule> findByLoanIdAndStatusOrderByPaymentNumberAsc(String loanId, PaymentSchedule.PaymentStatus status);
    
    @Query("SELECT ps FROM PaymentSchedule ps WHERE ps.loan.id = :loanId AND ps.status = 'PENDING' ORDER BY ps.paymentNumber ASC")
    List<PaymentSchedule> findPendingPaymentsByLoanId(@Param("loanId") String loanId);

    PaymentSchedule findFirstByLoanIdAndDueDateBeforeAndStatus(String loanId, Long dueDate, PaymentSchedule.PaymentStatus status);

    PaymentSchedule findFirstByLoanIdAndStatusOrderByPaymentNumberAsc(String loanId, PaymentSchedule.PaymentStatus status);
} 