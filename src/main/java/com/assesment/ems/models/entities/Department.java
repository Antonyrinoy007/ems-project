package com.assesment.ems.models.entities;

import com.assesment.ems.models.dtos.DepartmentDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.util.List;

@Entity
@Table(name = "departments")
@Getter
@Setter
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Date creationDate;

    @OneToOne
    @JoinColumn(name = "head_id")
    private Employee departmentHead;

    @OneToMany(mappedBy = "department")
    private List<Employee> employees;
    public DepartmentDTO toDTO() {
        DepartmentDTO dto = new DepartmentDTO();
        dto.setId(this.id);
        dto.setName(this.name);
        dto.setCreationDate(this.creationDate);
        dto.setDepartmentHeadId(this.departmentHead != null ? this.departmentHead.getId() : null);
        return dto;
    }
}
