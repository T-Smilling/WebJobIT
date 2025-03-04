package com.javaweb.jobIT.dto.response.company;

import com.javaweb.jobIT.dto.response.jobpost.JobPostResponseForCompany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyResponse {
    private Long id;
    private String companyName;
    private String description;
    private String website;
    private String logoUrl;
    private String address;
    private Long numberOfEmployees;
    private List<JobPostResponseForCompany> jobPosts;
}
