package com.devops.leavemgmt.service;

import com.devops.leavemgmt.model.*;
import com.devops.leavemgmt.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class LeaveService {

    private final LeaveRequestRepository leaveRepo;
    private final EmployeeRepository empRepo;

    public LeaveService(LeaveRequestRepository leaveRepo, EmployeeRepository empRepo) {
        this.leaveRepo = leaveRepo;
        this.empRepo = empRepo;
    }

    public LeaveRequest submitLeave(Long employeeId, LeaveRequest request) {
        Employee employee = empRepo.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found: " + employeeId));

        long days = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;

        if (request.getLeaveType() == LeaveType.ANNUAL && employee.getAnnualLeaveBalance() < days) {
            throw new RuntimeException("Insufficient leave balance. Available: "
                + employee.getAnnualLeaveBalance() + " days, Requested: " + days + " days");
        }

        request.setEmployee(employee);
        request.setStatus(LeaveStatus.PENDING);
        return leaveRepo.save(request);
    }

    @Transactional
    public LeaveRequest reviewLeave(Long leaveId, Long managerId, LeaveStatus decision) {
        LeaveRequest leave = leaveRepo.findById(leaveId)
            .orElseThrow(() -> new RuntimeException("Leave request not found: " + leaveId));

        Employee manager = empRepo.findById(managerId)
            .orElseThrow(() -> new RuntimeException("Manager not found: " + managerId));

        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new RuntimeException("Leave request is already " + leave.getStatus());
        }

        leave.setStatus(decision);
        leave.setReviewedBy(manager);

        if (decision == LeaveStatus.APPROVED && leave.getLeaveType() == LeaveType.ANNUAL) {
            long days = ChronoUnit.DAYS.between(leave.getStartDate(), leave.getEndDate()) + 1;
            Employee emp = leave.getEmployee();
            emp.setAnnualLeaveBalance(emp.getAnnualLeaveBalance() - (int) days);
            empRepo.save(emp);
        }

        return leaveRepo.save(leave);
    }

    public List<LeaveRequest> getAllLeaves() { return leaveRepo.findAll(); }
    public List<LeaveRequest> getLeavesByEmployee(Long empId) { return leaveRepo.findByEmployeeId(empId); }
    public List<LeaveRequest> getPendingLeaves() { return leaveRepo.findByStatus(LeaveStatus.PENDING); }
}