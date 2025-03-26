package com.assesment.ems.models.entities;

import com.assesment.ems.models.dtos.DepartmentDTO;
import com.assesment.ems.models.dtos.EmployeeDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Entity
@Table(name = "employees")
@Getter
@Setter
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Date dateOfBirth;
    private double salary;
    private String address;
    private String role;
    private Date joiningDate;
    private Double yearlyBonusPercentage;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Employee reportingManager;

    public EmployeeDTO toDTO() {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(this.id);
        dto.setName(this.name);
        dto.setSalary(this.salary);
        dto.setAddress(this.address);
        dto.setRole(this.role);
        dto.setJoiningDate(this.joiningDate);
        dto.setYearlyBonusPercentage(this.yearlyBonusPercentage);
        dto.setDepartment(this.department != null ? this.department.toDTO() : null);
        dto.setReportingManager(this.reportingManager != null ? this.reportingManager.toDTO() : null);
        return dto;
    }


}