package com.assesment.ems.services;

import com.assesment.ems.Repos.DepartmentRepository;
import com.assesment.ems.Repos.EmployeeRepository;
import com.assesment.ems.models.dtos.EmployeeDTO;
import com.assesment.ems.models.dtos.EmployeeLookupDTO;
import com.assesment.ems.models.entities.Department;
import com.assesment.ems.models.entities.Employee;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    private final DepartmentRepository departmentRepository;
    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    public EmployeeService(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }
    @Transactional
    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        logger.info("Creating new employee: {}", employeeDTO.getName());
        Department department = null;

        if (employeeDTO.getDepartment() != null) {
            logger.info("Handling department: {}", employeeDTO.getDepartment().getName());
            department = departmentRepository.findByName(employeeDTO.getDepartment().getName())
                    .orElseGet(() -> {
                        Department newDepartment = employeeDTO.getDepartment().toEntity(null); // Initially no department head
                        return departmentRepository.save(newDepartment);
                    });
        }

        Employee reportingManager = null;

        if (employeeDTO.getReportingManager() != null) {
            logger.info("Handling reporting manager: {}", employeeDTO.getReportingManager().getName());
            reportingManager = employeeRepository.findByName(employeeDTO.getReportingManager().getName())
                    .orElseGet(() -> {
                        Employee newManager = employeeDTO.getReportingManager().toEntity(null, null);
                        return employeeRepository.save(newManager);
                    });
        }

        if (department != null && reportingManager != null && department.getDepartmentHead() == null) {
            department.setDepartmentHead(reportingManager);
            department = departmentRepository.save(department);
        }

        logger.info("Converting DTO to Entity and saving employee");
        Employee employee = employeeDTO.toEntity(department, reportingManager);

        employee.setDepartment(department);
        employee.setReportingManager(reportingManager);

        Employee savedEmployee = employeeRepository.save(employee);

        logger.info("Employee created successfully with ID: {}", savedEmployee.getId());
        return savedEmployee.toDTO();
    }

    @Transactional
    public EmployeeDTO updateEmployee(Long employeeId, EmployeeDTO employeeDTO) {
        logger.info("Updating employee with ID: {}", employeeId);

        // Fetch existing employee
        Employee existingEmployee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with ID: " + employeeId));

        // Update basic employee details
        existingEmployee.setName(employeeDTO.getName());
        existingEmployee.setSalary(employeeDTO.getSalary());
        existingEmployee.setAddress(employeeDTO.getAddress());
        existingEmployee.setRole(employeeDTO.getRole());
        existingEmployee.setJoiningDate(employeeDTO.getJoiningDate());
        existingEmployee.setYearlyBonusPercentage(employeeDTO.getYearlyBonusPercentage());

        if (employeeDTO.getDepartment() != null) {
            logger.info("Handling department: {}", employeeDTO.getDepartment().getName());
            Department department = departmentRepository.findByName(employeeDTO.getDepartment().getName())
                    .orElseGet(() -> {
                        Department newDepartment = employeeDTO.getDepartment().toEntity(null);
                        return departmentRepository.save(newDepartment);
                    });

            // Update department head if needed
            if (department.getDepartmentHead() == null && existingEmployee.getReportingManager() != null) {
                department.setDepartmentHead(existingEmployee.getReportingManager());
                department = departmentRepository.save(department);
            }

            existingEmployee.setDepartment(department);
        }

        if (employeeDTO.getReportingManager() != null) {
            logger.info("Handling reporting manager: {}", employeeDTO.getReportingManager().getName());
            Employee reportingManager = employeeRepository.findByName(employeeDTO.getReportingManager().getName())
                    .orElseGet(() -> {
                        Employee newManager = employeeDTO.getReportingManager().toEntity(null, null);
                        return employeeRepository.save(newManager);
                    });

            existingEmployee.setReportingManager(reportingManager);
        }

        Employee updatedEmployee = employeeRepository.save(existingEmployee);
        logger.info("Employee updated successfully with ID: {}", updatedEmployee.getId());

        return updatedEmployee.toDTO();
    }


    public Page<EmployeeDTO> getAllEmployees(Pageable pageable) {
        logger.info("Fetching all employees");
        return employeeRepository.findAll(pageable).map(Employee::toDTO);
    }
    @Transactional
    public EmployeeDTO updateEmployeeDepartment(Long employeeId, Long newDepartmentId) {
        logger.info("Moving employee ID {} to department ID {}", employeeId, newDepartmentId);

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

        Department newDepartment = departmentRepository.findById(newDepartmentId)
                .orElseThrow(() -> new EntityNotFoundException("Department not found"));

        employee.setDepartment(newDepartment);
        return employeeRepository.save(employee).toDTO();
    }
    public List<EmployeeLookupDTO> getEmployeeLookup() {
        logger.info("Fetching employee name and ID lookup");
        return employeeRepository.findAll().stream()
                .map(emp -> new EmployeeLookupDTO(emp.getId(), emp.getName()))
                .collect(Collectors.toList());
    }

}
