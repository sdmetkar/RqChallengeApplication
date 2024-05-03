package com.example.rqchallenge.employees;

import com.example.rqchallenge.exception.EmployeeNotFoundException;
import com.example.rqchallenge.model.Employee;
import com.example.rqchallenge.service.IEmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class EmployeeControllerImpl implements IEmployeeController {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeControllerImpl.class);

    private final IEmployeeService employeeService;

    @Autowired
    public EmployeeControllerImpl(IEmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() throws IOException {
        List<Employee> employees = employeeService.getAllEmployees();
        if (employees != null) {
            return ResponseEntity.ok(employees);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        if (searchString == null || searchString.isEmpty()) {
            throw new IllegalArgumentException("Search string can not be empty");
        }

        List<Employee> matchedEmployees = employeeService.getEmployeesByNameSearch(searchString);
        return ResponseEntity.ok(matchedEmployees);
    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(int id) {
        validateId(id);

        Employee employee = employeeService.getEmployeeById(id);
        if (employee != null) {
            return ResponseEntity.ok(employee);
        } else {
            throw new EmployeeNotFoundException("Employee not found with id: " + id);
        }
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        int highestSalary = employeeService.getHighestSalaryOfEmployees();
        return ResponseEntity.ok(highestSalary);
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        List<String> topTenNames = employeeService.getHighestEarningEmployeeNames(10);
        return ResponseEntity.ok(topTenNames);
    }

    @Override
    public ResponseEntity<Employee> createEmployee(Map<String, Object> employeeInput) {

        Employee employee = employeeService.createEmployee(employeeInput);
        if (employee != null) {
            return ResponseEntity.ok(employee);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(int id) {
        validateId(id);
        String message = employeeService.deleteEmployeeById(id);
        return ResponseEntity.ok(message);
    }

    private void validateId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid id");
        }
    }
    
}
