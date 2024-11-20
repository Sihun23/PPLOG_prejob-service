package myaong.popolog.prejobsservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class AdminPrejobRequest {

    @Getter
    public static class UpdateIndex {
        @NotNull(message = "순서를 입력해주세요.")
        private Integer index;
    }

    @Getter
    public static class AddJob {
        @NotNull(message = "카테고리 ID를 입력해주세요.")
        private Long categoryId;

        @NotBlank(message = "직군 이름을 입력해주세요.")
        private String name;
    }

    @Getter
    public static class UpdateName {
        @NotBlank(message = "직군 이름을 입력해주세요.")
        private String name;
    }
}
