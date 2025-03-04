package com.javaweb.jobIT.controller;

import com.javaweb.jobIT.dto.request.job.JobPostRequest;
import com.javaweb.jobIT.dto.request.job.JobPostUpdateRequest;
import com.javaweb.jobIT.dto.request.job.JobSearchCriteria;
import com.javaweb.jobIT.dto.response.ApiResponse;
import com.javaweb.jobIT.dto.response.jobpost.JobPostResponse;
import com.javaweb.jobIT.dto.response.jobpost.PageJobPost;
import com.javaweb.jobIT.service.JobPostService;
import com.javaweb.jobIT.service.SubscriberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "/jobs")
@Slf4j
@RequiredArgsConstructor
public class JobPostController {
    private final JobPostService jobPostService;
    private final SubscriberService subscriberService;

    @PostMapping
    ApiResponse<JobPostResponse> createJob(@RequestBody @Valid JobPostRequest jobPostRequest) {
        return ApiResponse.<JobPostResponse>builder()
                .result(jobPostService.createJob(jobPostRequest))
                .message("Create job successful")
                .build();
    }

    @GetMapping
    public ApiResponse<PageJobPost> getSearchJobs(@RequestParam Map<String, String> params,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size) {

        JobSearchCriteria criteria = new JobSearchCriteria(params);
        return ApiResponse.<PageJobPost>builder()
                .result(jobPostService.searchJobs(criteria,page, size))
                .message("Find job successful")
                .build();
    }

    @GetMapping("/all-jobs")
    ApiResponse<PageJobPost> getAllJob(@RequestParam(defaultValue = "0", required = false) int page,
                                       @RequestParam(defaultValue = "10", required = false) int size) {
        return ApiResponse.<PageJobPost>builder()
                .result(jobPostService.getAllJobPost(page, size))
                .message("Get all job successful")
                .build();
    }

    @PostMapping("/send-mail/{jobId}")
    ApiResponse<String> sendMailToSubscriber(@PathVariable Long jobId) {
        return ApiResponse.<String>builder()
                .result(subscriberService.findSubscriberToSendMail(jobId))
                .message("Send successful")
                .build();
    }

    @GetMapping("/{jobId}")
    ApiResponse<JobPostResponse> getJobById(@PathVariable Long jobId) {
        return ApiResponse.<JobPostResponse>builder()
                .result(jobPostService.getJobPostById(jobId))
                .message("Get job by id: " + jobId + "successful")
                .build();
    }

    @PutMapping("/{jobId}")
    ApiResponse<JobPostResponse> updateJob(@PathVariable Long jobId,@RequestBody @Valid JobPostUpdateRequest jobPostRequest) {
        return ApiResponse.<JobPostResponse>builder()
                .result(jobPostService.updateInfoJob(jobId, jobPostRequest))
                .message("Update job successful")
                .build();
    }

    @DeleteMapping("/{jobId}")
    ApiResponse<String> deleteJob(@PathVariable Long jobId) {
        return ApiResponse.<String>builder()
                .result(jobPostService.deleteJobPostById(jobId))
                .build();
    }
}
