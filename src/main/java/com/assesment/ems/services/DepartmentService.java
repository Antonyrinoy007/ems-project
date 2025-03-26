package com.assesment.ems.services;

import com.assesment.ems.Repos.DepartmentRepository;
import com.assesment.ems.Repos.EmployeeRepository;
import com.assesment.ems.models.dtos.DepartmentDTO;
import com.assesment.ems.models.dtos.EmployeeDTO;
import com.assesment.ems.models.entities.Department;
import com.assesment.ems.models.entities.Employee;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    public DepartmentService(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public DepartmentDTO createDepartment(DepartmentDTO departmentDTO) {
        logger.info("Creating new department: {}", departmentDTO.getName());

        Department department = new Department();
        department.setName(departmentDTO.getName());
        department.setCreationDate(departmentDTO.getCreationDate() != null ? departmentDTO.getCreationDate() : new Date(System.currentTimeMillis()));
        if (departmentDTO.getDepartmentHeadId() != null) {
            Employee departmentHead = employeeRepository.findById(departmentDTO.getDepartmentHeadId())
                    .orElseThrow(() -> new EntityNotFoundException("Department Head not found"));
            department.setDepartmentHead(departmentHead);
        }

        Department savedDepartment = departmentRepository.save(department);
        return savedDepartment.toDTO();
    }
    @Transactional
    public DepartmentDTO updateDepartment(Long id, DepartmentDTO departmentDTO) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Department not found with ID: " + id));

        department.setName(departmentDTO.getName());
        department.setCreationDate(departmentDTO.getCreationDate() != null ? departmentDTO.getCreationDate() : new Date(System.currentTimeMillis()));

        if (departmentDTO.getDepartmentHeadId() != null) {
            Employee departmentHead = employeeRepository.findById(departmentDTO.getDepartmentHeadId())
                    .orElseThrow(() -> new EntityNotFoundException("Employee not found with ID: " + departmentDTO.getDepartmentHeadId()));
            department.setDepartmentHead(departmentHead);
        } else {
            department.setDepartmentHead(null);
        }

        Department updatedDepartment = departmentRepository.save(department);
        return updatedDepartment.toDTO();
    }

    @Transactional
    public void deleteDepartment(Long id) {
        logger.info("Deleting department with ID: {}", id);

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Department not found"));

        if (!department.getEmployees().isEmpty()) {
            throw new IllegalStateException("Cannot delete department with assigned employees.");
        }

        departmentRepository.delete(department);
    }

    public Page<DepartmentDTO> getAllDepartments(Pageable pageable) {
        return departmentRepository.findAll(pageable).map(Department::toDTO);
    }

    public DepartmentDTO getDepartmentById(Long id, boolean expandEmployees) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Department not found with ID: " + id));

        DepartmentDTO departmentDTO = department.toDTO();

        if (expandEmployees) {
            List<EmployeeDTO> employees = department.getEmployees()
                    .stream()
                    .map(Employee::toDTO)
                    .collect(Collectors.toList());
            departmentDTO.setEmployees(employees);
        }

        return departmentDTO;
    }
}
