package com.doittogether.platform.presentation.dto.fcm;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "FCM 토큰 저장 및 업데이트 요청 DTO")
public record SaveOrUpdateTokenRequest(

        @NotBlank(message = "토큰은 필수 입력 값입니다.")
        @Schema(description = "디바이스 토큰", example = "device_token_value")
        String token,

        @NotBlank(message = "디바이스 플랫폼 타입은 필수 입력 값입니다.")
        @Schema(description = "디바이스 플랫폼 타입", example = "ANDROID")
        String platformType
) {}