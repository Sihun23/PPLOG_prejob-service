package myaong.popolog.prejobsservice.service;

import lombok.RequiredArgsConstructor;
import myaong.popolog.prejobsservice.common.exception.ApiCode;
import myaong.popolog.prejobsservice.common.exception.ApiException;
import myaong.popolog.prejobsservice.dto.request.PrejobRequest;
import myaong.popolog.prejobsservice.dto.response.PrejobResponse;
import myaong.popolog.prejobsservice.entity.Job;
import myaong.popolog.prejobsservice.entity.PreferredJob;
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
    private final JobRepository jobRepository;

    // 카테고리별 선택 가능한 직군 목록 조회
    @Transactional(readOnly = true)
    public List<PrejobResponse> getAvailableJobs() {
        List<Job> jobs = jobRepository.findAll();

        if (jobs.isEmpty()) {
            throw new ApiException(ApiCode.CATEGORY_NOT_FOUND, "존재하지 않는 카테고리입니다.");
        }

        return jobs.stream()
                .collect(Collectors.groupingBy(job -> job.getCategory().getName())) // 카테고리별 그룹화
                .entrySet().stream()
                .map(entry -> new PrejobResponse(
                        entry.getKey(), // 카테고리 이름
                        entry.getValue().stream()
                                .sorted((job1, job2) -> job1.getIndex().compareTo(job2.getIndex())) // index 기준 정렬
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
                .map(preferredJob -> new PrejobResponse.JobDetail(preferredJob.getJob().getId(), preferredJob.getJob().getName()))
                .collect(Collectors.toList());
    }

    // 관심 직군 저장
    public void savePrejobs(Long memberId, PrejobRequest request) {
        if (request.getPreJob().size() > 5 || request.getPreJob().size() < 1) {
            throw new ApiException(ApiCode.INVALID_DATA, "관심 직군은 최소 1개, 최대 5개까지 선택할 수 있습니다.");
        }

        // 기존 데이터 삭제 후 새로운 관심 직군 저장하는 방식
        List<PreferredJob> existingJobs = preferredJobRepository.findByMemberId(memberId);
        preferredJobRepository.deleteAll(existingJobs);

        preferredJobRepository.flush();

        List<PreferredJob> newPreferredJobs = request.getPreJob().stream()
                .distinct()
                .map(jobId -> PreferredJob.builder()
                        .memberId(memberId)
                        .job(jobRepository.findById(jobId)
                                .orElseThrow(() -> new ApiException(ApiCode.JOB_NOT_FOUND, "존재하지 않는 직군입니다: " + jobId)))
                        .build())
                .collect(Collectors.toList());

        preferredJobRepository.saveAll(newPreferredJobs);
    }

    // 특정 회원의 모든 관심 직군 삭제
    public void deletePrejobs(Long memberId) {
        List<PreferredJob> preferredJobs = preferredJobRepository.findByMemberId(memberId);
        preferredJobRepository.deleteAll(preferredJobs);
    }
}