package com.doittogether.platform.application.global.response;

import static lombok.AccessLevel.PRIVATE;

import com.doittogether.platform.application.global.code.ExceptionCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder(access = PRIVATE)
@Schema(description = "예외 응답 객체")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ExceptionResponse<T>(
        @Schema(description = "성공 여부", example = "false")
        boolean isSuccess,

        @Schema(description = "HTTP 상태 코드", example = "NOT FOUND")
        HttpStatus httpStatus,

        @Schema(description = "예외 코드", example = "COMMON_400")
        String code,

        @Schema(description = "예외 메시지", example = "입력값이 유효하지 않습니다.")
        String message,

        @Schema(description = "응답 데이터 (제네릭: 다양 형태의 응답 제공)", nullable = true)
        T result
) {
    public ExceptionResponse(ExceptionCode exceptionCode) {
        this(false, exceptionCode.getHttpStatus(), exceptionCode.getCode(), exceptionCode.getMessage(), null);
    }

    public static ExceptionResponse<Void> onFailure(ExceptionCode exceptionCode) {
        return new ExceptionResponse<>(exceptionCode);
    }
}
