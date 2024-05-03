package com.example.rqchallenge;

import com.example.rqchallenge.exception.InvalidEmployeeDataException;
import com.example.rqchallenge.model.Employee;
import com.example.rqchallenge.service.IEmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RqChallengeApplicationTests {


    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private IEmployeeService employeeService;

    @Test
    void contextLoads() {
    }

    @Test
    public void testGetAllEmployees() throws Exception {
        List<Employee> employees = List.of(new Employee(1, "Sunny", 20000, 34), new Employee(2, "Smit", 30000, 35));
        when(employeeService.getAllEmployees()).thenReturn(employees);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Sunny"))
                .andExpect(jsonPath("$[1].name").value("Smit"));

        verify(employeeService, times(1)).getAllEmployees();
    }

    @Test
    public void testGetEmployeeById() throws Exception {
        int id = 1;
        Employee employee = new Employee(id, "Sunny", 20000, 34);
        when(employeeService.getEmployeeById(id)).thenReturn(employee);

        mockMvc.perform(get("/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Sunny"));

        verify(employeeService, times(1)).getEmployeeById(id);
    }

    @Test
    public void testGetEmployeeById_invalidId() throws Exception {
        int id = -31;

        mockMvc.perform(get("/{id}", id))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message[1]").value("Invalid id"));

        verify(employeeService, times(0)).getEmployeeById(id);
    }

    @Test
    public void testCreateEmployee() throws Exception {
        Employee savedEmployee = new Employee(33, "Sunny", 20000, 34);
        Employee newEmployee = new Employee(0, "Sunny", 20000, 34);
        when(employeeService.createEmployee(any(Map.class))).thenReturn(savedEmployee);

        mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(newEmployee)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(33))
                .andExpect(jsonPath("$.name").value("Sunny"));

        verify(employeeService, times(1)).createEmployee(any(Map.class));
    }

    @Test
    public void testCreateEmployee_invalid_salary() throws Exception {
        Employee newEmployee = new Employee(0, "Sunny", -20000, 34);
        when(employeeService.createEmployee(any(Map.class))).thenThrow(new InvalidEmployeeDataException("Employee salary is mandatory and should be positive integer"));

        mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(newEmployee)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.message[0]").value("Employee salary is mandatory and should be positive integer"));

        verify(employeeService, times(1)).createEmployee(any(Map.class));
    }

    @Test
    public void testGetEmployeesByNameSearch() throws Exception {
        String searchString = "Sunny";
        List<Employee> employees = List.of(new Employee(1, "Sunny Metkar", 444444, 23), new Employee(2, "Sunnypqr Metkar", 4444, 22));
        when(employeeService.getEmployeesByNameSearch(searchString)).thenReturn(employees);

        mockMvc.perform(get("/search/{searchString}", searchString))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Sunny Metkar"))
                .andExpect(jsonPath("$[1].name").value("Sunnypqr Metkar"));

        verify(employeeService, times(1)).getEmployeesByNameSearch(searchString);
    }

    @Test
    public void testGetEmployeesByNameSearch_nullSearchString() throws Exception {
        String searchString = null;
        List<Employee> employees = List.of(new Employee(1, "Sunny Metkar", 444444, 23), new Employee(2, "Sunnypqr Metkar", 4444, 22));
        when(employeeService.getEmployeesByNameSearch(searchString)).thenReturn(employees);

        mockMvc.perform(get("/search/{searchString}", searchString))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.message[0]").value("Provided input is either empty or not in expected format or data type"));


        verify(employeeService, times(0)).getEmployeesByNameSearch(searchString);
    }

    @Test
    public void testGetHighestSalaryOfEmployees() throws Exception {
        int highestSalary = 100000;
        when(employeeService.getHighestSalaryOfEmployees()).thenReturn(highestSalary);

        mockMvc.perform(get("/highestSalary"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(highestSalary));

        verify(employeeService, times(1)).getHighestSalaryOfEmployees();
    }

    @Test
    public void testDeleteEmployeeById() throws Exception {
        int id = 1;
        when(employeeService.deleteEmployeeById(id)).thenReturn("Employee deleted successfully");

        mockMvc.perform(delete("/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().string("Employee deleted successfully"));

        verify(employeeService, times(1)).deleteEmployeeById(id);
    }

    @Test
    public void testGetTopTenHighestEarningEmployeeNames() throws Exception {
        List<String> employeeNames = List.of("Sunny Metkar", "Shahrukh Khan", "Sachin Tendulkar", "xyz Metkar1", "paaa ddd", "Sachin Tendulfdkar1", "xyz Metkdfar1", "xyz Mdddetkar1", "xyz Mddetkar1", "xyz ff");
        when(employeeService.getHighestEarningEmployeeNames(10)).thenReturn(employeeNames);

        mockMvc.perform(get("/topTenHighestEarningEmployeeNames"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(10))
                .andExpect(jsonPath("$[0]").value("Sunny Metkar"))
                .andExpect(jsonPath("$[1]").value("Shahrukh Khan"))
                .andExpect(jsonPath("$[2]").value("Sachin Tendulkar"));

        verify(employeeService, times(1)).getHighestEarningEmployeeNames(10);
    }
}
