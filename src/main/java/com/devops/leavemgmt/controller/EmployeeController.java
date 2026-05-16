package com.devops.leavemgmt.controller;

import com.devops.leavemgmt.model.Employee;
import com.devops.leavemgmt.repository.EmployeeRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeRepository repo;

    public EmployeeController(EmployeeRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Employee create(@Valid @RequestBody Employee employee) {
        return repo.save(employee);
    }

    @GetMapping
    public List<Employee> getAll() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public Employee getById(@PathVariable Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Employee not found: " + id));
    }

    @GetMapping("/{id}/balance")
    public Map<String, Object> getBalance(@PathVariable Long id) {
        Employee emp = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Employee not found: " + id));
        return Map.of(
            "employee", emp.getName(),
            "annualLeaveBalance", emp.getAnnualLeaveBalance()
        );
    }
}