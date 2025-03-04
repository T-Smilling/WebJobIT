package com.javaweb.jobIT.dto.request.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.javaweb.jobIT.validator.DobConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    private String password;

    @DobConstraint(min = 10, message = "INVALID_DOB")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dob;
    private String fullName;
    private String phone;
    private MultipartFile avatarUrl;
    private List<String> roles;
}
