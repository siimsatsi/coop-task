package com.coop.loan.controller;

import com.coop.loan.dto.SystemParameterRequest;
import com.coop.loan.dto.SystemParameterResponse;
import com.coop.loan.model.SystemParameter;
import com.coop.loan.service.SystemParameterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parameters")
@RequiredArgsConstructor
@Tag(name = "Parameters", description = "System parameter management")
public class SystemParameterController {

    private final SystemParameterService systemParameterService;

    @GetMapping
    @Operation(summary = "Get all system parameters")
    public List<SystemParameterResponse> getAll() {
        return systemParameterService.getAll();
    }

    @PutMapping("/{key}")
    @Operation(summary = "Update a system parameter")
    public SystemParameterResponse update(@PathVariable String key,
                                          @Valid @RequestBody SystemParameterRequest request) {
        SystemParameter updated = systemParameterService.update(key, request.getValue());
        return toResponse(updated);
    }

    private SystemParameterResponse toResponse(SystemParameter param) {
        SystemParameterResponse response = new SystemParameterResponse();
        response.setKey(param.getKey());
        response.setValue(param.getValue());
        response.setDescription(param.getDescription());
        return response;
    }
}
