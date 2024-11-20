package myaong.popolog.prejobsservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AdminCategoryJobResponse {
    private String categoryName;
    private List<JobDetail> jobs;

    @Getter
    @AllArgsConstructor
    public static class JobDetail {
        private Long jobId;
        private String jobName;
        private Integer index;
        private Integer memberCount;
    }
}