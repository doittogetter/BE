package com.doittogether.platform.presentation.dto.discord;

import lombok.Builder;

@Builder
public record DiscordEmbedDto(String title, String description) {
}
