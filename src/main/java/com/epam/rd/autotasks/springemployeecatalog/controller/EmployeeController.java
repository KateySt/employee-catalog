package com.epam.rd.autotasks.springemployeecatalog.controller;

import com.epam.rd.autotasks.springemployeecatalog.domain.Employee;
import com.epam.rd.autotasks.springemployeecatalog.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public List<Employee> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sort) {
        return employeeService.getAllEmployees(page, size, sort).getContent();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "false") boolean full_chain) {
        return employeeService.getEmployeeById(id, full_chain)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by_manager/{managerId}")
    public List<Employee> getEmployeesByManager(
            @PathVariable Long managerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort) {
        return employeeService.getEmployeesByManager(managerId, page, size, sort).getContent();
    }

    @GetMapping("/by_department/{departmentIdentifier}")
    public List<Employee> getEmployeesByDepartment(
            @PathVariable String departmentIdentifier,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort) {
        return employeeService.getEmployeesByDepartment(departmentIdentifier, page, size, sort).getContent();
    }
}