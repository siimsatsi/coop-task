package com.coop.loan.mapper;

import com.coop.loan.dto.LoanApplicationRequest;
import com.coop.loan.dto.LoanApplicationResponse;
import com.coop.loan.dto.PaymentScheduleEntryResponse;
import com.coop.loan.model.Loan;
import com.coop.loan.model.PaymentScheduleEntry;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoanMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "rejectionReason", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "paymentSchedule", ignore = true)
    Loan toEntity(LoanApplicationRequest request);

    LoanApplicationResponse toResponse(Loan loan);

    PaymentScheduleEntryResponse toResponse(PaymentScheduleEntry entry);
}
