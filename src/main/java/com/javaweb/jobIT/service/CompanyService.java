package com.javaweb.jobIT.service;

import com.javaweb.jobIT.constant.RoleEnum;
import com.javaweb.jobIT.dto.request.company.CompanyRequest;
import com.javaweb.jobIT.dto.response.company.CompanyResponse;
import com.javaweb.jobIT.dto.response.company.PageCompany;
import com.javaweb.jobIT.dto.response.jobpost.CompanyForJobResponse;
import com.javaweb.jobIT.dto.response.jobpost.JobPostResponseForCompany;
import com.javaweb.jobIT.entity.CompanyEntity;
import com.javaweb.jobIT.entity.EmployeeEntity;
import com.javaweb.jobIT.entity.RoleEntity;
import com.javaweb.jobIT.entity.UserEntity;
import com.javaweb.jobIT.exception.AppException;
import com.javaweb.jobIT.exception.ErrorCode;
import com.javaweb.jobIT.exception.ResourceNotFoundException;
import com.javaweb.jobIT.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final JobApplicationService jobApplicationService;
    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;
    private final UploadImageService uploadImageService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserEntity getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        String name = authentication.getName();
        return userRepository.findByUsername(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    private CompanyResponse convertResponse(CompanyEntity company) {
        CompanyResponse companyResponse = modelMapper.map(company, CompanyResponse.class);
        companyResponse.setNumberOfEmployees((company.getEmployees() == null || company.getEmployees().isEmpty()) ? 0L : company.getEmployees().size());
        companyResponse.setJobPosts((companyResponse.getJobPosts() == null) ? new ArrayList<>() : getListJob(company));
        return companyResponse;
    }

    // tạo công ty thì người đó cũng là thành viên
    @PreAuthorize("hasAnyRole('EMPLOYER','ADMIN')")
    public CompanyResponse createCompany(CompanyRequest request) throws IOException {
        CompanyEntity company = modelMapper.map(request, CompanyEntity.class);
        company.setLogoUrl(uploadImageService.uploadImage(request.getLogoUrl()));
        CompanyEntity savedCompany = companyRepository.save(company);

        UserEntity user = jobApplicationService.getUser();
        EmployeeEntity employee = EmployeeEntity.builder()
                .company(savedCompany)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .build();

        employeeRepository.save(employee);

        savedCompany.getEmployees().add(employee);

        return convertResponse(savedCompany);
    }

    private List<JobPostResponseForCompany> getListJob(CompanyEntity company){
        return company.getJobPosts().stream().map(jobPost ->
                JobPostResponseForCompany.builder()
                        .title(jobPost.getTitle())
                        .description(jobPost.getDescription())
                        .numberOfJobApplications(jobApplicationRepository.countJobApplicationsByJobPostId(jobPost.getId()))
                        .build()
        ).collect(Collectors.toList());
    }

    public PageCompany getAllCompany(int page, int size){
        Pageable pageable = PageRequest.of(page,size, Sort.by("companyName").ascending());
        Page<CompanyResponse> result = companyRepository.findAll(pageable).map(this::convertResponse);

        return PageCompany.builder()
                .content(result.getContent())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .build();
    }

    public CompanyResponse getCompanyById(Long id){
        CompanyEntity company = companyRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        return convertResponse(company);
    }

    public CompanyForJobResponse getCompanyForJob(Long id){
        CompanyEntity company = companyRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        return CompanyForJobResponse.builder()
                .id(company.getId())
                .companyName(company.getCompanyName())
                .build();
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYER')")
    public CompanyResponse updateInfoCompany(Long id, CompanyRequest request) throws IOException {
        CompanyEntity company = companyRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        UserEntity user = getUser();
        EmployeeEntity employee = employeeRepository.findByEmail(user.getEmail()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        RoleEntity roleEmployee = roleRepository.findById(String.valueOf(RoleEnum.EMPLOYER)).orElseThrow(()-> new ResourceNotFoundException("Role not found"));
        if (!company.getEmployees().contains(employee) && user.getRoles().contains(roleEmployee)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        CompanyEntity updatedCompany = modelMapper.map(company, CompanyEntity.class);
        updatedCompany.setEmployees(company.getEmployees());
        updatedCompany.setJobPosts(company.getJobPosts());
        updatedCompany.setLogoUrl(uploadImageService.uploadImage(request.getLogoUrl()));
        companyRepository.save(updatedCompany);
        return convertResponse(updatedCompany);
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYER')")
    public String deleteCompanyById(Long id){
        CompanyEntity company = companyRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        UserEntity user = getUser();
        EmployeeEntity employee = employeeRepository.findByEmail(user.getEmail()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        RoleEntity roleEmployee = roleRepository.findById(String.valueOf(RoleEnum.EMPLOYER)).orElseThrow(()-> new ResourceNotFoundException("Role not found"));
        if (!company.getEmployees().contains(employee) && user.getRoles().contains(roleEmployee)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        companyRepository.deleteById(id);
        return "Company deleted";
    }
}
