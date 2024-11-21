package myaong.popolog.prejobsservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import myaong.popolog.prejobsservice.common.exception.ApiResponse;
import myaong.popolog.prejobsservice.dto.request.PrejobRequest;
import myaong.popolog.prejobsservice.dto.response.PrejobResponse;
import myaong.popolog.prejobsservice.service.PrejobService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pre-jobs")
@RequiredArgsConstructor
public class PrejobController {

    private final PrejobService prejobService;

    @Operation(summary = "API 명세서 v0.5 line 21", description = "관심직군 목록 조회")
    @GetMapping("/jobs")
    public ResponseEntity<ApiResponse<List<PrejobResponse>>> getAvailableJobs() {
        List<PrejobResponse> response = prejobService.getAvailableJobs();
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "API 명세서 v0.5 line 22", description = "회원의 관심직군 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<List<PrejobResponse.JobDetail>>> getMemberPrejobs(@RequestHeader(name = "memberId") Long memberId) {
        List<PrejobResponse.JobDetail> response = prejobService.getPrejobsForMember(memberId);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "API 명세서 v0.5 line 23", description = "관심직군 등록/수정")
    @PostMapping
    public ResponseEntity<ApiResponse<Object>> savePrejobs(@RequestHeader(name = "memberId") Long memberId, @Valid @RequestBody PrejobRequest prejobRequest) {
        prejobService.savePrejobs(memberId, prejobRequest);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    /*
    @Operation(summary = "서비스간 API v0.4 line 3", description = "회원의 관심직군 삭제")
    @DeleteMapping("/{memberId}")
    public ResponseEntity<ApiResponse<Object>> deleteMemberPrejobs(@PathVariable Long memberId) {
        prejobService.deletePrejobs(memberId);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }
     */
}