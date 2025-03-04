package com.javaweb.jobIT.controller;

import com.javaweb.jobIT.dto.request.employee.EmployeeRequest;
import com.javaweb.jobIT.dto.response.ApiResponse;
import com.javaweb.jobIT.dto.response.company.EmployerInCompanyResponse;
import com.javaweb.jobIT.dto.response.company.EmployerResponse;
import com.javaweb.jobIT.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/employee")
@Slf4j
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    @PostMapping("/{companyId}")
    public ApiResponse<EmployerResponse> addEmployerInCompany(@PathVariable Long companyId,
                                                              @RequestBody @Valid EmployeeRequest employeeRequest) {
        return ApiResponse.<EmployerResponse>builder()
                .result(employeeService.addEmployerInCompany(companyId,employeeRequest))
                .message("Add employee for company successful")
                .build();
    }

    @GetMapping("/{companyId}")
    public ApiResponse<EmployerInCompanyResponse> getEmployerInCompany(@PathVariable Long companyId,
                                                                       @RequestParam(defaultValue = "0", required = false) int page,
                                                                       @RequestParam(defaultValue = "10", required = false) int size) {
        return ApiResponse.<EmployerInCompanyResponse>builder()
                .result(employeeService.getEmployerInCompany(companyId,page, size))
                .message("Get all employee in company successful")
                .build();
    }

    @DeleteMapping("/{companyId}/{employeeId}")
    ApiResponse<String> deleteEmployee(@PathVariable Long companyId, @PathVariable Long employeeId) {
        return ApiResponse.<String>builder()
                .result(employeeService.deleteEmployerInCompany(companyId,employeeId))
                .build();
    }
}
