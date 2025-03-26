package com.assesment.ems.models.dtos;

import com.assesment.ems.models.entities.Department;
import com.assesment.ems.models.entities.Employee;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
@Getter
@Setter
@Schema(description = "DTO representing an employee")
public class EmployeeDTO {
    @Schema(description = "Unique ID of the employee", example = "1")
    private Long id;
    @Schema(description = "Full name of the employee", example = "John Doe")
    private String name;
    @Schema(description = "Date of birth of the employee (YYYY-MM-DD)", example = "1990-05-15")
    private String dob;
    @Schema(description = "Salary of the employee", example = "75000")
    private Double salary;
    @Schema(description = "Address of the employee", example = "1234 Elm Street")
    private String address;
    @Schema(description = "Job role/title", example = "Software Engineer")
    private String role;
    @Schema(description = "Joining date of the employee (YYYY-MM-DD)", example = "2020-07-01")
    private Date joiningDate;
    @Schema(description = "Yearly bonus percentage", example = "10.5")
    private Double yearlyBonusPercentage;
    @Schema(description = "Department detials")
    private DepartmentDTO department;
    private EmployeeDTO reportingManager;
    private Date creationDate;

    public Employee toEntity(Department department, Employee reportingManager) {
        Employee employee = new Employee();
        employee.setId(this.id);
        employee.setName(this.name);
        employee.setSalary(this.salary);
        employee.setAddress(this.address);
        employee.setRole(this.role);
        employee.setJoiningDate(this.joiningDate);
        employee.setDepartment(department);
        employee.setReportingManager(reportingManager);
        return employee;
    }

}