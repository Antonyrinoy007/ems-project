package com.assesment.ems.controllers;

import com.assesment.ems.Repos.EmployeeRepository;
import com.assesment.ems.models.dtos.EmployeeDTO;
import com.assesment.ems.models.dtos.EmployeeLookupDTO;
import com.assesment.ems.models.entities.Employee;
import com.assesment.ems.services.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }
    @Operation(summary = "Create a new employee", description = "Creates a new employee record and returns the created employee details.")
    @PostMapping
    public ResponseEntity<EmployeeDTO> createEmployee(@RequestBody EmployeeDTO employee) throws Exception {
        return new ResponseEntity<>(employeeService.createEmployee(employee), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDTO> updateEmployee(@PathVariable Long id, @RequestBody EmployeeDTO employeeDetails) throws Exception {
        return ResponseEntity.ok(employeeService.updateEmployee(id, employeeDetails));
    }
    @Operation(summary = "Get all employee list", description = "Fetches employee details as list")
    @GetMapping
    public ResponseEntity<?> getEmployees(@RequestParam(required = false, defaultValue = "false") boolean lookup,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "20") int size) {
        if (lookup) {
            return ResponseEntity.ok(employeeService.getEmployeeLookup());
        }
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(employeeService.getAllEmployees(pageable));
    }
    @PatchMapping("/{employeeId}/department/{newDepartmentId}")
    @Operation(summary = "Update Employee Department", description = "Moves an employee from one department to another")
    public ResponseEntity<EmployeeDTO> updateEmployeeDepartment(@PathVariable Long employeeId, @PathVariable Long newDepartmentId) {
        return ResponseEntity.ok(employeeService.updateEmployeeDepartment(employeeId, newDepartmentId));
    }
    @GetMapping("/lookup")
    @Operation(summary = "List Employee Names & IDs", description = "Lists employee names and IDs when lookup=true is passed as a parameter")
    public ResponseEntity<List<EmployeeLookupDTO>> getEmployeeLookup() {
        return ResponseEntity.ok(employeeService.getEmployeeLookup());
    }
}
