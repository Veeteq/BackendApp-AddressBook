package com.veeteq.addressbook.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("employee")
public class Employee extends Person<Employee> {
	@Column(name="cont_job_tx")
    private String job;

    @Column(name="cont_slry_nm", columnDefinition = "NUMERIC(10,2)",precision = 10, scale = 2)
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
