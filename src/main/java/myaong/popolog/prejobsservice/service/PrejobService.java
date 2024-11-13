package myaong.popolog.prejobsservice.service;

import lombok.RequiredArgsConstructor;
import myaong.popolog.prejobsservice.common.exception.ApiCode;
import myaong.popolog.prejobsservice.common.exception.ApiException;
import myaong.popolog.prejobsservice.dto.request.PrejobRequest;
import myaong.popolog.prejobsservice.dto.response.PrejobResponse;
import myaong.popolog.prejobsservice.entity.Category;
import myaong.popolog.prejobsservice.entity.Job;
import myaong.popolog.prejobsservice.entity.PreferredJob;
import myaong.popolog.prejobsservice.repository.CategoryRepository;
import myaong.popolog.prejobsservice.repository.JobRepository;
import myaong.popolog.prejobsservice.repository.PreferredJobRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PrejobService {

    private final PreferredJobRepository preferredJobRepository;
    private final CategoryRepository categoryRepository;
    private final JobRepository jobRepository;

    // 카테고리별 선택 가능한 직군 목록 조회
    @Transactional(readOnly = true)
    public List<PrejobResponse> getAvailableJobs() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(category -> new PrejobResponse(
                        category.getName(),
                        category.getJobs().stream()
                                .map(job -> new PrejobResponse.JobDetail(job.getId(), job.getName()))
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }


    // 회원의 관심 직군 조회
    @Transactional(readOnly = true)
    public List<PrejobResponse.JobDetail> getPrejobsForMember(Long memberId) {
        List<PreferredJob> preferredJobs = preferredJobRepository.findByMemberId(memberId);
        return preferredJobs.stream()
                .map(job -> new PrejobResponse.JobDetail(job.getJob().getId(), job.getJob().getName()))
                .collect(Collectors.toList());
    }

    // 관심 직군 저장
    public void savePrejobs(Long memberId, PrejobRequest request) {
        if (request.getPreJob().size() > 5 || request.getPreJob().size() < 1) {
            throw new ApiException(ApiCode.INVALID_DATA, "관심 직군은 최소 1개, 최대 5개까지 선택할 수 있습니다."); // 컨벤션에 맞는 메시지
        }

        // 기존 데이터 삭제 후 저장 (idempotent 방식 구현)
        preferredJobRepository.deleteAll(preferredJobRepository.findByMemberId(memberId));

        // 중복된 직군 이름 확인 (JOB_DUPLICATED 처리)
        for (Long jobId : request.getPreJob()) {
            if (preferredJobRepository.findByMemberId(memberId).stream()
                    .anyMatch(preferredJob -> preferredJob.getJob().getId().equals(jobId))) {
                throw new ApiException(ApiCode.JOB_DUPLICATED, "이미 존재하는 직군입니다: " + jobId); // 중복 검사 예외 메시지 추가
            }
        }

        List<PreferredJob> newPreferredJobs = request.getPreJob().stream()
                .map(jobId -> PreferredJob.builder()
                        .memberId(memberId)
                        .job(jobRepository.findById(jobId)
                                .orElseThrow(() -> new ApiException(ApiCode.JOB_CONFLICT, "존재하지 않는 직군 ID입니다: " + jobId)))
                        .build())
                .collect(Collectors.toList());
        preferredJobRepository.saveAll(newPreferredJobs);
    }

    // 카테고리 삭제
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ApiException(ApiCode.CATEGORY_CONFLICT, "해당 카테고리에 속하는 직군이 있어 삭제할 수 없습니다."));
        if (!category.getJobs().isEmpty()) {
            throw new ApiException(ApiCode.CATEGORY_CONFLICT, "해당 카테고리에 속하는 직군이 있어 삭제할 수 없습니다.");
        }
        categoryRepository.delete(category);
    }

    // 관심 직군 삭제
    public void deletePrejobs(Long memberId) {
        List<PreferredJob> preferredJobs = preferredJobRepository.findByMemberId(memberId);
        if (preferredJobs.isEmpty()) {
            throw new ApiException(ApiCode.JOB_CONFLICT, "관심 직군이 존재하지 않습니다.");
        }
        preferredJobRepository.deleteAll(preferredJobs);
    }
}
