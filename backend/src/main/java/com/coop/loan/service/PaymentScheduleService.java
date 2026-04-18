package com.coop.loan.service;

import com.coop.loan.model.Loan;
import com.coop.loan.model.PaymentScheduleEntry;
import com.coop.loan.repository.PaymentScheduleEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentScheduleService {

    private static final MathContext MC = new MathContext(10, RoundingMode.HALF_UP);
    private static final int SCALE = 2;

    private final PaymentScheduleEntryRepository scheduleRepository;

    @Transactional
    public void generate(Loan loan) {
        BigDecimal annualRate = loan.getInterestMargin().add(loan.getBaseInterestRate());
        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(100), MC)
                .divide(BigDecimal.valueOf(12), MC);

        BigDecimal principal = loan.getLoanAmount();
        int termMonths = loan.getTermMonths();

        BigDecimal monthlyPayment = calculateMonthlyPayment(principal, monthlyRate, termMonths);

        List<PaymentScheduleEntry> entries = new ArrayList<>();
        BigDecimal remainingBalance = principal;
        LocalDate paymentDate = LocalDate.now();

        for (int i = 1; i <= termMonths; i++) {
            BigDecimal interestPayment = remainingBalance.multiply(monthlyRate, MC)
                    .setScale(SCALE, RoundingMode.HALF_UP);
            BigDecimal principalPayment = monthlyPayment.subtract(interestPayment)
                    .setScale(SCALE, RoundingMode.HALF_UP);

            if (i == termMonths) {
                principalPayment = remainingBalance;
                monthlyPayment = principalPayment.add(interestPayment);
            }

            remainingBalance = remainingBalance.subtract(principalPayment)
                    .setScale(SCALE, RoundingMode.HALF_UP);

            entries.add(PaymentScheduleEntry.builder()
                    .loan(loan)
                    .paymentNumber(i)
                    .paymentDate(paymentDate)
                    .principal(principalPayment)
                    .interest(interestPayment)
                    .totalPayment(monthlyPayment)
                    .remainingBalance(remainingBalance.max(BigDecimal.ZERO))
                    .build());

            paymentDate = paymentDate.plusMonths(1);
        }

        scheduleRepository.saveAll(entries);
        loan.getPaymentSchedule().addAll(entries);
    }

    private BigDecimal calculateMonthlyPayment(BigDecimal principal, BigDecimal monthlyRate, int termMonths) {
        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            return principal.divide(BigDecimal.valueOf(termMonths), SCALE, RoundingMode.HALF_UP);
        }
        BigDecimal onePlusRate = BigDecimal.ONE.add(monthlyRate);
        BigDecimal pow = onePlusRate.pow(termMonths, MC);
        BigDecimal numerator = principal.multiply(monthlyRate, MC).multiply(pow, MC);
        BigDecimal denominator = pow.subtract(BigDecimal.ONE);
        return numerator.divide(denominator, SCALE, RoundingMode.HALF_UP);
    }
}
