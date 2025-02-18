package com.javaweb.userservice.controller;

import com.javaweb.userservice.dto.request.PermissionRequest;
import com.javaweb.userservice.dto.response.ApiResponse;
import com.javaweb.userservice.dto.response.PermissionResponse;
import com.javaweb.userservice.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/permissions")
@Slf4j
@RequiredArgsConstructor
public class PermissionController {
    private final PermissionService permissionService;

    @PostMapping
    ApiResponse<PermissionResponse> createPermission(@RequestBody PermissionRequest request) {
        return ApiResponse.<PermissionResponse>builder()
                .result(permissionService.createPermission(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<PermissionResponse>> getAllPermissions() {
        return ApiResponse.<List<PermissionResponse>>builder()
                .result(permissionService.getAllPermissions())
                .build();
    }

    @DeleteMapping("/{permission}")
    ApiResponse<Void> deletePermission(@PathVariable String permission) {
        permissionService.deletePermission(permission);
        return ApiResponse.<Void>builder()
                .message("Delete successfully")
                .build();
    }
}
