package com.doittogether.platform.presentation.dto.reaction;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "FCM 토큰 활성화 상태 응답")
public record CheckTokenResponse(

        @Schema(description = "토큰의 활성화 상태 여부", example = "true")
        boolean isActive

) {}