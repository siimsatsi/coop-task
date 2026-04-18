package com.coop.loan.controller;

import com.coop.loan.dto.LoanApplicationRequest;
import com.coop.loan.dto.LoanApplicationResponse;
import com.coop.loan.dto.LoanRejectRequest;
import com.coop.loan.mapper.LoanMapper;
import com.coop.loan.model.Loan;
import com.coop.loan.service.LoanReviewService;
import com.coop.loan.service.LoanSubmissionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
@Tag(name = "Loans", description = "Loan application and review endpoints")
public class LoanController {

    private final LoanSubmissionService submissionService;
    private final LoanReviewService reviewService;
    private final LoanMapper loanMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Submit a new loan application")
    public LoanApplicationResponse submit(@Valid @RequestBody LoanApplicationRequest request) {
        Loan loan = loanMapper.toEntity(request);
        return loanMapper.toResponse(submissionService.submit(loan));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a loan application by ID")
    public LoanApplicationResponse getById(@PathVariable UUID id) {
        return loanMapper.toResponse(reviewService.getById(id));
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "Approve a loan application")
    public LoanApplicationResponse approve(@PathVariable UUID id) {
        return loanMapper.toResponse(reviewService.approve(id));
    }

    @PostMapping("/{id}/reject")
    @Operation(summary = "Reject a loan application")
    public LoanApplicationResponse reject(@PathVariable UUID id,
                                          @Valid @RequestBody LoanRejectRequest request) {
        return loanMapper.toResponse(reviewService.reject(id, request.getReason()));
    }

    @GetMapping
    @Operation(summary = "Get all loans in review")
    public List<LoanApplicationResponse> getAllInReview() {
        return reviewService.getAllInReview().stream()
                .map(loanMapper::toResponse)
                .toList();
    }
}
