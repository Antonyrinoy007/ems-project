package com.assesment.ems.controllers;

import com.assesment.ems.models.dtos.DepartmentDTO;
import com.assesment.ems.services.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/departments")
@Tag(name = "Department Management", description = "APIs for managing departments")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @Operation(summary = "Add Department", description = "Creates a new department")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Department created successfully"),
    })
    @PostMapping
    public ResponseEntity<DepartmentDTO> addDepartment(@RequestBody @Valid DepartmentDTO departmentDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(departmentService.createDepartment(departmentDTO));
    }

    @Operation(summary = "Delete Department", description = "Deletes a department if no employees are assigned to it")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Department deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Cannot delete department with assigned employees"),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDepartment(@PathVariable Long id) {
        try {
            departmentService.deleteDepartment(id);
            return ResponseEntity.ok("Department deleted successfully.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Fetch All Departments", description = "Retrieves all departments with pagination")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Departments retrieved successfully"),
    })
    @GetMapping
    public ResponseEntity<Page<DepartmentDTO>> getAllDepartments(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(departmentService.getAllDepartments(pageable));
    }
    @PutMapping("/{id}")
    public ResponseEntity<DepartmentDTO> updateDepartment(
            @PathVariable Long id,
            @RequestBody @Valid DepartmentDTO departmentDTO) {
        DepartmentDTO updatedDepartment = departmentService.updateDepartment(id, departmentDTO);
        return ResponseEntity.ok(updatedDepartment);
    }

    @Operation(summary = "Get Department Details", description = "Fetches a department by ID. If `expand=employee` is provided, it includes all employees in the department.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Department retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Department not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<DepartmentDTO> getDepartmentById(
            @PathVariable Long id,
            @RequestParam(value = "expand", required = false) String expand) {
        boolean expandEmployees = "employee".equalsIgnoreCase(expand);
        DepartmentDTO departmentDTO = departmentService.getDepartmentById(id, expandEmployees);
        return ResponseEntity.ok(departmentDTO);
    }
}
