package com.doittogether.platform.business.discord;


import com.doittogether.platform.presentation.dto.discord.DiscordMessageDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "discordSignUpClientApi",
        url = "${discord.webhook.signup.url}")
public interface DiscordSignUpClientApi {
    @PostMapping()
    void sendAlarm(@RequestBody DiscordMessageDto discordMessageDto);
}
