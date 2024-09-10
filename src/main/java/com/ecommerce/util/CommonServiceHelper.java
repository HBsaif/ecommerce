package com.ecommerce.util;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CommonServiceHelper {
	@PersistenceContext
    private EntityManager entityManager;

    /**
     * Executes a stored procedure and returns the result as a map of output parameters.
     * 
     * @param procedureName The name of the stored procedure to execute.
     * @param parameters A map of input parameters where key is the parameter name and value is the parameter value.
     * @return A map of output parameters where key is the parameter name and value is the parameter value.
     */
	public Map<String, Object> executeStoredProcedure(String procedureName, Map<String, Object> parameters) {
        log.info("Executing stored procedure '{}' with parameters: {}", procedureName, parameters);

        // Create the StoredProcedureQuery instance
        StoredProcedureQuery storedProcedureQuery = entityManager.createStoredProcedureQuery(procedureName);

        // Set input parameters and register output parameters
        parameters.forEach((key, value) -> {
            if (value instanceof Map) {
                // Handle output parameters
                Class<?> type = (Class<?>) ((Map<?, ?>) value).get("type");
                storedProcedureQuery.registerStoredProcedureParameter(key, type, ParameterMode.OUT);
            } else {
                // Handle input parameters
                storedProcedureQuery.registerStoredProcedureParameter(key, value.getClass(), ParameterMode.IN);
                storedProcedureQuery.setParameter(key, value);
            }
        });

        // Execute the stored procedure
        storedProcedureQuery.execute();

        // Collect output parameters
        Map<String, Object> outputParameters = parameters.entrySet().stream()
            .filter(entry -> entry.getValue() instanceof Map)
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> {
                    Object output = storedProcedureQuery.getOutputParameterValue(entry.getKey());
                    log.info("Output parameter '{}' received with value: {}", entry.getKey(), output);
                    return output;
                }
            ));

        log.info("Stored procedure '{}' executed successfully. Output parameters: {}", procedureName, outputParameters);

        return outputParameters;
    }
}
