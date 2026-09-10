package com.veeteq.addressbook.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class Employee extends Person<Employee> {
	@Column(name="job")
    private String job;

    @Column(name="salary", columnDefinition = "NUMERIC(10,2)",precision = 10, scale = 2)
    private BigDecimal salary;

    public Employee() {}

    public String getJob() {
        return job;
    }

    public Employee setJob(String job) {
        this.job = job;
        return this;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public Employee setSalary(BigDecimal salary) {
        this.salary = salary;
        return this;
    }
}
