package com.doittogether.platform.presentation.dto.personality;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
public record PersonalityResponse(
        @Schema(description = "분석 결과 키워드", example = "[\n"
                + "    \"무난함\uD83D\uDC4C\",\n"
                + "    \"활동적인\uD83C\uDFC3\u200D♀\uFE0F\",\n"
                + "    \"조용함\uD83E\uDD2B\",\n"
                + "    \"스트레스\uD83E\uDD2F\"\n"
                + "  ]")
        List<String> keywords
) {
    public static PersonalityResponse from(List<String> keywords) {
        return PersonalityResponse.builder()
                .keywords(keywords)
                .build();
    }
}
