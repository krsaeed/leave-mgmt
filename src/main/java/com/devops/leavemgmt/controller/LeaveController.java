package com.devops.leavemgmt.controller;

import com.devops.leavemgmt.model.LeaveRequest;
import com.devops.leavemgmt.model.LeaveStatus;
import com.devops.leavemgmt.service.LeaveService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/leaves")
public class LeaveController {

    private final LeaveService leaveService;

    public LeaveController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    @PostMapping("/employee/{employeeId}")
    @ResponseStatus(HttpStatus.CREATED)
    public LeaveRequest submit(@PathVariable Long employeeId,
                               @RequestBody LeaveRequest request) {
        return leaveService.submitLeave(employeeId, request);
    }

    @GetMapping
    public List<LeaveRequest> getAll() {
        return leaveService.getAllLeaves();
    }

    @GetMapping("/pending")
    public List<LeaveRequest> getPending() {
        return leaveService.getPendingLeaves();
    }

    @GetMapping("/employee/{employeeId}")
    public List<LeaveRequest> getByEmployee(@PathVariable Long employeeId) {
        return leaveService.getLeavesByEmployee(employeeId);
    }

    @PutMapping("/{leaveId}/review")
    public LeaveRequest review(@PathVariable Long leaveId,
                               @RequestParam Long managerId,
                               @RequestParam LeaveStatus decision) {
        return leaveService.reviewLeave(leaveId, managerId, decision);
    }
}