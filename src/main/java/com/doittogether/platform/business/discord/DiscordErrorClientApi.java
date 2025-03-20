package com.doittogether.platform.business.discord;

import com.doittogether.platform.presentation.dto.discord.DiscordMessageDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "discordErrorClientApi",
        url = "${discord.webhook.error.url}")
public interface DiscordErrorClientApi {
    @PostMapping()
    void sendAlarm(@RequestBody DiscordMessageDto discordMessageDto);
}
