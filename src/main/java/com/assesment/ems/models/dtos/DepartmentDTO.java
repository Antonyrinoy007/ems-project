package com.assesment.ems.models.dtos;

import com.assesment.ems.models.entities.Department;
import com.assesment.ems.models.entities.Employee;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.util.List;

@Getter
@Setter
public class DepartmentDTO {
    private Long id;
    private String name;
    private Date creationDate;
    private Long departmentHeadId;
    private List<EmployeeDTO> employees;

    public Department toEntity(Employee departmentHead) {
        Department department = new Department();
        department.setId(this.id);
        department.setName(this.name);
        department.setCreationDate(this.creationDate);
        department.setDepartmentHead(departmentHead);
        return department;
    }
}
