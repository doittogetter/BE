package com.doittogether.platform.presentation.dto.reaction;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "FCM 토큰 활성화 상태 확인 요청")
public record CheckTokenRequest(

        @NotBlank(message = "FCM 토큰은 필수입니다.")
        @Schema(description = "확인할 FCM 토큰")
        String token
) {}