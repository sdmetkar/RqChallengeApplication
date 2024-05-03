package com.example.rqchallenge.model;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.Objects;

public class Employee {
    private int id;

    @JsonAlias({"employee_name", "name"})
    private String name;

    @JsonAlias({"employee_salary", "salary"})
    private int salary;

    @JsonAlias({"employee_age", "age"})
    private int age;


    public Employee() {
        //noarg constructor required by framework
    }

    public Employee(int id, String name, int salary, int age) {
        this.id = id;
        this.name = name;
        this.salary = salary;
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(id, employee.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
