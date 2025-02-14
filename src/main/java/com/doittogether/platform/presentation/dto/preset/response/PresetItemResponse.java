package com.doittogether.platform.presentation.dto.preset.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "프리셋 아이템 항목 응답")
@Builder
public record PresetItemResponse(
        @Schema(description = "프리셋 아이템 아이디")
        Long presetItemId,

        @Schema(description = "프리셋 아이템 이름", example = "바닥 청소")
        String name
) {
    public static PresetItemResponse of(Long preseItemtId, String name) {
        return PresetItemResponse.builder()
                .presetItemId(preseItemtId)
                .name(name)
                .build();
    }
}