package com.javaweb.jobIT.service;

import com.javaweb.jobIT.dto.request.employee.EmployeeRequest;
import com.javaweb.jobIT.dto.response.company.EmployerInCompanyResponse;
import com.javaweb.jobIT.dto.response.company.EmployerResponse;
import com.javaweb.jobIT.entity.CompanyEntity;
import com.javaweb.jobIT.entity.EmployeeEntity;
import com.javaweb.jobIT.entity.UserEntity;
import com.javaweb.jobIT.exception.AppException;
import com.javaweb.jobIT.exception.ErrorCode;
import com.javaweb.jobIT.exception.ResourceNotFoundException;
import com.javaweb.jobIT.repository.CompanyRepository;
import com.javaweb.jobIT.repository.EmployeeRepository;
import com.javaweb.jobIT.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final CompanyRepository companyRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    public UserEntity getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        String name = authentication.getName();
        return userRepository.findByUsername(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    //  Thêm nhân viên vào công ty;
    @PreAuthorize("hasAnyRole('EMPLOYER','ADMIN')")
    public EmployerResponse addEmployerInCompany(Long companyId, EmployeeRequest employeeRequest) {
        Optional<EmployeeEntity> existEmployee = employeeRepository.findByEmail(employeeRequest.getEmail());
        if (existEmployee.isPresent()) throw new AppException(ErrorCode.USER_EXISTED);

        CompanyEntity companyEntity = companyRepository.findById(companyId).orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        UserEntity user = getUser();
        if (!companyEntity.getCreatedBy().equals(user.getUsername()) && checkRoleAdmin()) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        EmployeeEntity employee = modelMapper.map(employeeRequest, EmployeeEntity.class);
        employee.setCompany(companyEntity);
        employee = employeeRepository.save(employee);
        companyEntity.getEmployees().add(employee);

        companyRepository.save(companyEntity);
        return modelMapper.map(employee, EmployerResponse.class);
    }

    @PreAuthorize("hasAnyRole('EMPLOYER','ADMIN')")
    public EmployerInCompanyResponse getEmployerInCompany(Long companyId,int page,int size) {
        Pageable pageable = PageRequest.of(page,size);
        CompanyEntity companyEntity = companyRepository.findById(companyId).orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        UserEntity user = getUser();
        if (!companyEntity.getCreatedBy().equals(user.getUsername()) && checkRoleAdmin()) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        Page<EmployeeEntity> company = employeeRepository.findByCompany_Id(companyId,pageable);
        Page<EmployerResponse> result = company.map(employer -> modelMapper.map(employer, EmployerResponse.class));

        return EmployerInCompanyResponse.builder()
                .companyName(companyEntity.getCompanyName())
                .content(result.getContent())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .build();
    }

    @PreAuthorize("hasAnyRole('EMPLOYER','ADMIN')")
    public String deleteEmployerInCompany(Long companyId, Long employerId) {
        if (!employeeRepository.existsByIdAndCompany_Id(employerId, companyId)) {
            throw new RuntimeException("Employee not found for this company");
        }
        CompanyEntity companyEntity = companyRepository.findById(companyId).orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        UserEntity user = getUser();
        if (!companyEntity.getCreatedBy().equals(user.getUsername()) && checkRoleAdmin()) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        employeeRepository.deleteById(employerId);
        return "Employee deleted";
    }

    private boolean checkRoleAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().noneMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }
}
