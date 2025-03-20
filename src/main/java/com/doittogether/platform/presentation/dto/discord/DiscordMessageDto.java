package com.doittogether.platform.presentation.dto.discord;

import java.util.List;
import lombok.Builder;

@Builder
public record DiscordMessageDto(String content, List<DiscordEmbedDto> embeds
) {
}
