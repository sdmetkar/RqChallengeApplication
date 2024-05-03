package com.example.rqchallenge.service;

import com.example.rqchallenge.exception.InvalidEmployeeDataException;
import com.example.rqchallenge.model.Employee;
import com.example.rqchallenge.model.ResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements IEmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    // NOTE : After spring version upgrade, WebClient can be used instead of RestTemplate for non-blocking more performant application
    private final RestTemplate restTemplate;


    @Value("${EXTERNAL_API_URL}")
    private String EXTERNAL_API_URL;

    @Autowired
    public EmployeeServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<Employee> getAllEmployees() {
        ResponseEntity<ResponseDto<List<Employee>>> responseEntity;
        try {
            responseEntity = restTemplate.exchange(
                    EXTERNAL_API_URL + "/employees",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<ResponseDto<List<Employee>>>() {
                    }
            );

            if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null && responseEntity.getBody().getData() != null) {
                return responseEntity.getBody().getData();
            }
        } catch (RestClientException e) {
            logger.error("Error occurred while fetching employees", e);
            throw e;
        }
        return null;
    }

    @Override
    public List<Employee> getEmployeesByNameSearch(String searchString) {
        if (searchString == null || searchString.isEmpty()) {
            throw new IllegalArgumentException("Search string can not be empty");
        }
        List<Employee> matchedEmployees = new ArrayList<>();
        try {
            List<Employee> employees = getAllEmployees();
            if (employees != null) {
                for (Employee employee : employees) {
                    // Check if the employee name contains or matches the search string
                    if (employee.getName().toLowerCase().contains(searchString.toLowerCase())) {
                        matchedEmployees.add(employee);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error fetching employees by name search: " + e.getMessage());
            throw e;
        }
        return matchedEmployees;
    }

    @Override
    public Employee getEmployeeById(int id) {
        validateId(id);
        ResponseEntity<ResponseDto<Employee>> responseEntity = restTemplate.exchange(EXTERNAL_API_URL + "/employee/" + id, HttpMethod.GET, null, new ParameterizedTypeReference<ResponseDto<Employee>>() {
        });

        if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null && responseEntity.getBody().getData() != null) {
            return responseEntity.getBody().getData();
        }
        return null;
    }

    @Override
    public int getHighestSalaryOfEmployees() {
        try {
            List<Employee> employees = getAllEmployees();
            if (employees != null) {
                return findHighestSalary(employees);
            }

        } catch (Exception e) {
            logger.error("Error fetching highest salary of employees: " + e.getMessage());
            throw e;
        }

        return Integer.MIN_VALUE;
    }

    @Override
    public List<String> getHighestEarningEmployeeNames(int count) {
        try {
            List<Employee> employees = getAllEmployees();
            if (employees != null) {
                // Sort employees based on their salaries in descending order
                List<Employee> sortedEmployees = employees.stream()
                        .sorted((e1, e2) -> Integer.compare(e2.getSalary(), e1.getSalary()))
                        .collect(Collectors.toList());

                // Get the names of the top ten highest-earning employees
                return sortedEmployees.stream()
                        .limit(count)
                        .map(Employee::getName)
                        .collect(Collectors.toList());
            }

        } catch (Exception e) {
            logger.error("Error fetching top ten highest earning employee names: " + e.getMessage());
            throw e;
        }

        return new ArrayList<>();
    }

    @Override
    public Employee createEmployee(Map<String, Object> employeeInput) {
        validateEmployeeInput(employeeInput);

        // Set the request headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Create the request body from the employee input map
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(employeeInput, headers);
        // Make a POST request to create the employee

        ResponseEntity<ResponseDto<Employee>> responseEntity = restTemplate.exchange(EXTERNAL_API_URL + "/create", HttpMethod.POST, request, new ParameterizedTypeReference<ResponseDto<Employee>>() {
        });
        if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null && responseEntity.getBody().getData() != null) {
            return responseEntity.getBody().getData();
        }
        return null;
    }

    @Override
    public String deleteEmployeeById(int id) {
        validateId(id);
        // Create request headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create the request entity with headers
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // Send a DELETE request to delete the employee
        ResponseEntity<String> responseEntity = restTemplate.exchange(EXTERNAL_API_URL + "/delete/" + id, HttpMethod.DELETE, requestEntity, String.class);
        return responseEntity.getBody();
    }

    private int findHighestSalary(List<Employee> employees) {
        return employees.stream()
                .mapToInt(Employee::getSalary)
                .max()
                .orElse(Integer.MIN_VALUE);
    }


    private void validateEmployeeInput(Map<String, Object> employeeInput) {
        if (employeeInput == null || employeeInput.isEmpty()) {
            throw new InvalidEmployeeDataException("Employee details missing");
        }
        if (!employeeInput.containsKey("name") || employeeInput.get("name") == null) {
            throw new InvalidEmployeeDataException("Employee name is mandatory");
        }
        if (!employeeInput.containsKey("age") || isNonPositiveInteger(employeeInput.get("age"))) {
            throw new InvalidEmployeeDataException("Employee age is mandatory and should be positive integer");
        }
        if (!employeeInput.containsKey("salary") || isNonPositiveInteger(employeeInput.get("salary"))) {
            throw new InvalidEmployeeDataException("Employee salary is mandatory and should be positive integer");
        }

    }

    private boolean isNonPositiveInteger(Object value) {

        return value == null || !isStringInteger(value.toString()) || Integer.parseInt(value.toString()) <= 0;
    }

    private boolean isStringInteger(String str) {
        try {
            // Attempt to parse the string as an integer
            Integer.parseInt(str);
            return true; // Parsing successful
        } catch (NumberFormatException e) {
            // String cannot be parsed as an integer
            return false;
        }
    }

    private void validateId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid id");
        }
    }
}
