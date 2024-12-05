package myaong.popolog.prejobsservice.service;

import lombok.RequiredArgsConstructor;
import myaong.popolog.prejobsservice.common.exception.ApiCode;
import myaong.popolog.prejobsservice.common.exception.ApiException;
import myaong.popolog.prejobsservice.dto.request.AdminPrejobRequest;
import myaong.popolog.prejobsservice.dto.response.AdminCategoryJobResponse;
import myaong.popolog.prejobsservice.entity.Category;
import myaong.popolog.prejobsservice.entity.Job;
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
public class AdminJobService {

    private final JobRepository jobRepository;
    private final CategoryRepository categoryRepository;
    private final PreferredJobRepository preferredJobRepository;

    @Transactional(readOnly = true)
    public List<AdminCategoryJobResponse> getJobCategories() {
        return categoryRepository.findAll().stream()
                .map(category -> new AdminCategoryJobResponse(
                        category.getId(),
                        category.getName(),
                        category.getJobs().stream()
                                .sorted((job1, job2) -> job1.getIndex().compareTo(job2.getIndex())) // index 기준 정렬
                                .map(job -> new AdminCategoryJobResponse.JobDetail(
                                        job.getId(),
                                        job.getName(),
                                        job.getIndex(),
                                        (int) preferredJobRepository.countByJob(job)
                                ))
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }

    public void updateJobIndex(Long jobId, Integer newIndex) {
        Job targetJob = jobRepository.findById(jobId)
                .orElseThrow(() -> new ApiException(ApiCode.JOB_NOT_FOUND));

        Integer currentIndex = targetJob.getIndex();
        Category category = targetJob.getCategory();

        // 충돌 방지를 위해 임시값 -999로 변경
        targetJob.updateIndex(-999);
        jobRepository.save(targetJob);

        if (currentIndex < newIndex) {
            // 위로 이동 -> 인덱스 감소 처리
            jobRepository.updateIndexRangeDecrement(category, currentIndex + 1, newIndex);
        } else if (currentIndex > newIndex) {
            // 아래로 이동 -> 인덱스 증가 처리
            jobRepository.updateIndexRangeIncrement(category, newIndex, currentIndex - 1);
        }

        // 최종 인덱스로 업데이트
        targetJob.updateIndex(newIndex);
        jobRepository.save(targetJob);
    }


    public AdminCategoryJobResponse.JobDetail addJob(AdminPrejobRequest.AddJob request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ApiException(ApiCode.CATEGORY_NOT_FOUND));

        // 중복된 jobName 확인
        boolean exists = jobRepository.existsByCategoryAndName(category, request.getName());
        if (exists) {
            throw new ApiException(ApiCode.JOB_DUPLICATED, "해당 카테고리 내에 이미 존재하는 직군 이름입니다.");
        }


        Job newJob = Job.builder()
                .category(category)
                .name(request.getName())
                .index(category.getJobs().size() + 1)
                .build();

        Job savedJob = jobRepository.save(newJob);
        return new AdminCategoryJobResponse.JobDetail(
                savedJob.getId(),
                savedJob.getName(),
                savedJob.getIndex(),
                0
        );
    }




    public void updateJobName(Long jobId, String name) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ApiException(ApiCode.JOB_NOT_FOUND));
        job.updateName(name); // 이름 업데이트
        jobRepository.save(job); // 변경 사항 저장
    }

    public void deleteJob(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ApiException(ApiCode.JOB_NOT_FOUND));
        int memberCount = preferredJobRepository.countByJob(job);
        if (memberCount > 0) {
            throw new ApiException(ApiCode.JOB_CONFLICT, "선택된 회원이 있는 직군은 삭제할 수 없습니다.");
        }
        jobRepository.delete(job);
    }
}
