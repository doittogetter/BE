package com.doittogether.platform.presentation.dto.fcm;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "토큰 삭제 메세지 DTO")
public record RemoveTokenRequest(

        @NotBlank(message = "토큰은 필수 입력 값입니다.")
        @Schema(description = "디바이스 토큰", example = "device_token_value")
        String token
) {
}
