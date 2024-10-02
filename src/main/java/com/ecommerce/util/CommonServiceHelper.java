package com.ecommerce.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
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

	    // Register IN parameters first
	    parameters.forEach((key, value) -> {
	        if (!(value instanceof Map)) {
	            // Register IN parameter
	            storedProcedureQuery.registerStoredProcedureParameter(key, value.getClass(), ParameterMode.IN);
	            storedProcedureQuery.setParameter(key, value);
	        }
	    });

	    // Now register OUT parameters
	    parameters.forEach((key, value) -> {
	        if (value instanceof Map) {
	            // Register OUT parameter
	            Class<?> type = (Class<?>) ((Map<?, ?>) value).get("type");
	            storedProcedureQuery.registerStoredProcedureParameter(key, type, ParameterMode.OUT);
	        }
	    });

	    // Execute the stored procedure
	    storedProcedureQuery.execute();

	    // Collect output parameters if any exist
	    Map<String, Object> outputParameters = new HashMap<>();
	    parameters.forEach((key, value) -> {
	        if (value instanceof Map) {
	            Object output = storedProcedureQuery.getOutputParameterValue(key);
	            log.info("Output parameter '{}' received with value: {}", key, output);
	            outputParameters.put(key, output);
	        }
	    });

	    log.info("Stored procedure '{}' executed successfully. Output parameters: {}", procedureName, outputParameters);

	    return outputParameters;
	}

	
	
	
	// Method to execute a raw SQL update or insert
    public int executeUpdate(String sql, Map<String, Object> params) {
        Query query = entityManager.createNativeQuery(sql);

        // Set parameters
        if (params != null) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }

        return query.executeUpdate(); // Returns the number of affected rows
    }

    // Method to execute a raw SQL query and retrieve results
    public List<Object[]> executeQuery(String sql, Map<String, Object> params) {
    	log.info("Executing SQL Query : {} | params: {}", sql, params);
    	
        Query query = entityManager.createNativeQuery(sql);

        // Set parameters
        if (params != null) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }

        return query.getResultList(); // Returns a list of results
    }

}
