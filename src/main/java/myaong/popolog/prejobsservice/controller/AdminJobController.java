package myaong.popolog.prejobsservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import myaong.popolog.prejobsservice.common.exception.ApiResponse;
import myaong.popolog.prejobsservice.dto.request.AdminPrejobRequest;
import myaong.popolog.prejobsservice.dto.response.AdminCategoryJobResponse;
import myaong.popolog.prejobsservice.dto.response.CategoryJobResponse;
import myaong.popolog.prejobsservice.service.AdminJobService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/jobs")
@RequiredArgsConstructor
public class AdminJobController {

    private final AdminJobService adminJobService;

    @Operation(summary = "API 명세서 v0.4 line 116", description = "직군 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminCategoryJobResponse>>> getJobCategories(
            @RequestHeader(name = "memberId") Long memberId) {
        List<AdminCategoryJobResponse> response = adminJobService.getJobCategories();
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "API 명세서 v0.4 line 117", description = "직군 순서 변경")
    @PutMapping("/{jobId}/index")
    public ResponseEntity<ApiResponse<Object>> updateJobIndex(
            @RequestHeader(name = "memberId") Long memberId,
            @PathVariable Long jobId,
            @Valid @RequestBody AdminPrejobRequest.UpdateIndex request) {
        adminJobService.updateJobIndex(jobId, request.getIndex());
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    @Operation(summary = "API 명세서 v0.4 line 118", description = "신규 직군 추가")
    @PostMapping
    public ResponseEntity<ApiResponse<AdminCategoryJobResponse.JobDetail>> addJob(
            @RequestHeader(name = "memberId") Long memberId,
            @Valid @RequestBody AdminPrejobRequest.AddJob request) {
        AdminCategoryJobResponse.JobDetail response = adminJobService.addJob(request);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "API 명세서 v0.4 line 119", description = "직군 이름 변경")
    @PutMapping("/{jobId}")
    public ResponseEntity<ApiResponse<Object>> updateJobName(
            @RequestHeader(name = "memberId") Long memberId,
            @PathVariable Long jobId,
            @Valid @RequestBody AdminPrejobRequest.UpdateName request) {
        adminJobService.updateJobName(jobId, request.getName());
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    @Operation(summary = "API 명세서 v0.4 line 120", description = "직군 삭제")
    @DeleteMapping("/{jobId}")
    public ResponseEntity<ApiResponse<Object>> deleteJob(
            @RequestHeader(name = "memberId") Long memberId,
            @PathVariable Long jobId) {
        adminJobService.deleteJob(jobId);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }
}

