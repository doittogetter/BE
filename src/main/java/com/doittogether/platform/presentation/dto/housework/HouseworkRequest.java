package com.doittogether.platform.presentation.dto.housework;

import com.doittogether.platform.domain.enumeration.AssigneeStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "집안일 수정 요청 DTO")
public record HouseworkRequest(
        @NotNull(message = "카테고리 이름을 입력해주세요.")
        @Schema(description = "카테고리 이름", example = "거실")
        String category,

        @NotBlank(message = "작업 이름을 입력해주세요.")
        @Schema(description = "작업 이름", example = "먼지 닦기")
        String task,

        @NotNull(message = "시작 날짜와 시간을 입력해주세요. 패턴은 yyyy-MM-dd과 같습니다.")
        @Schema(description = "시작 날짜와 시간", example = "2024-11-23", type = "string")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        LocalDate startDate,

        @Nullable
        @Schema(description = "시작 시간", example = "00:00", type = "string")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
        LocalTime startTime,

        @NotNull(message = "담당자의 유저ID를 입력해주세요.")
        @Schema(description = "작업 담당자 ID", example = "1")
        Long userId,

        @NotNull(message = "담당자 할당의 상태를 입력해주세요")
        @Schema(description = "담당자 할당 상태" , allowableValues = {"INVALID", "VALID", "SELECT"})
        AssigneeStatus status
) {
    public static HouseworkRequest of(String category, String task, LocalDate startDate, LocalTime startTime,
                                      Boolean isAllDay,
                                      Long userId, AssigneeStatus status) {
        return new HouseworkRequest(category, task, startDate, startTime, userId,status);
    }
}
