package com.doittogether.platform.business.discord;

import com.doittogether.platform.domain.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("prd")
public class DiscordAlertSender {

    private final DiscordErrorClientApi discordErrorClientApi;
    private final DiscordSignUpClientApi discordSignUpClientApi;
    private final DiscordMessageGenerator discordMessageGenerator;

    public void sendDiscordAlarm(Exception exception) {
        try {
            discordErrorClientApi.sendAlarm(discordMessageGenerator.createMessage(exception));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void sendDiscordAlarm(User user) {
        try {
            log.debug("discord 알림 전송 중");
            discordSignUpClientApi.sendAlarm(discordMessageGenerator.createMessage(user));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
