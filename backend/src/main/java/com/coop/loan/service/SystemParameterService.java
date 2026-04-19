package com.coop.loan.service;

import com.coop.loan.dto.SystemParameterResponse;
import com.coop.loan.model.SystemParameter;
import com.coop.loan.repository.SystemParameterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SystemParameterService {

    private static final String EURIBOR_KEY = "euribor_6m";
    private static final String MAX_AGE_KEY = "max_customer_age";

    private final SystemParameterRepository repository;

    public BigDecimal getEuribor() {
        return get(EURIBOR_KEY);
    }

    public int getMaxCustomerAge() {
        return get(MAX_AGE_KEY).intValue();
    }

    public SystemParameter update(String key, BigDecimal value) {
        SystemParameter param = repository.findById(key)
                .orElseThrow(() -> new IllegalArgumentException("Parameter not found: " + key));
        param.setValue(value);
        return repository.save(param);
    }

    private BigDecimal get(String key) {
        return repository.findById(key)
                .map(SystemParameter::getValue)
                .orElseThrow(() -> new IllegalStateException("Missing system parameter: " + key));
    }

    public List<SystemParameterResponse> getAll() {
        return repository.findAll().stream()
                .map(p -> {
                    SystemParameterResponse r = new SystemParameterResponse();
                    r.setKey(p.getKey());
                    r.setValue(p.getValue());
                    r.setDescription(p.getDescription());
                    return r;
                })
                .toList();
    }
}
