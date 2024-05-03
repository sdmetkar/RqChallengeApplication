package com.example.rqchallenge.service;

import com.example.rqchallenge.model.Employee;

import java.util.List;
import java.util.Map;

public interface IEmployeeService {
    List<Employee> getAllEmployees();

    List<Employee> getEmployeesByNameSearch(String searchString);

    Employee getEmployeeById(int id);

    int getHighestSalaryOfEmployees();

    List<String> getHighestEarningEmployeeNames(int count);

    Employee createEmployee(Map<String, Object> employeeInput);

    String deleteEmployeeById(int id);
}
