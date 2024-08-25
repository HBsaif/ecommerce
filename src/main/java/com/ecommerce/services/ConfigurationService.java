package com.ecommerce.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.repositories.ConfigurationRepository;

@Service
public class ConfigurationService {

    @Autowired
    private ConfigurationRepository configurationRepository;

    public String getValue(String key) {
        return configurationRepository.findByKey(key)
                .orElseThrow(() -> new IllegalArgumentException("Configuration not found for key: " + key))
                .getValue();
    }
}

