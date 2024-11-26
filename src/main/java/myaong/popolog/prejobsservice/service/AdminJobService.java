package myaong.popolog.prejobsservice.service;

import lombok.RequiredArgsConstructor;
import myaong.popolog.prejobsservice.common.exception.ApiCode;
import myaong.popolog.prejobsservice.common.exception.ApiException;
import myaong.popolog.prejobsservice.dto.request.AdminPrejobRequest;
import myaong.popolog.prejobsservice.dto.response.AdminCategoryJobResponse;
import myaong.popolog.prejobsservice.dto.response.CategoryJobResponse;
import myaong.popolog.prejobsservice.dto.response.PrejobResponse;
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
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ApiException(ApiCode.JOB_NOT_FOUND));
        Job updatedJob = job.toBuilder(null, newIndex);
        jobRepository.save(updatedJob);
    }

    public AdminCategoryJobResponse.JobDetail addJob(AdminPrejobRequest.AddJob request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ApiException(ApiCode.CATEGORY_NOT_FOUND));
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
                0 // 초기 memberCount
        );
    }


    public void updateJobName(Long jobId, String name) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ApiException(ApiCode.JOB_NOT_FOUND));
        Job updatedJob = job.toBuilder(name, null);
        jobRepository.save(updatedJob);
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
