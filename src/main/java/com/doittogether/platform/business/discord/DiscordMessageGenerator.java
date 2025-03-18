package com.doittogether.platform.business.discord;

import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;

import com.doittogether.platform.domain.entity.User;
import com.doittogether.platform.presentation.dto.discord.DiscordEmbedDto;
import com.doittogether.platform.presentation.dto.discord.DiscordMessageDto;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class DiscordMessageGenerator {
    public DiscordMessageDto createMessage(Exception exception) {
        return DiscordMessageDto.builder()
                .content("## 🚨 서버 에러 발생 🚨")
                .embeds(List.of(DiscordEmbedDto.builder()
                                .title("ℹ️ 에러 정보")
                                .description("### 🕖 에러 발생 시간\n"
                                        + ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
                                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH시 mm분 ss초(서울 시간)"))
                                        + "\n"
                                        + "### 📜 에러 로그\n"
                                        + "```\n"
                                        + getStackTrace(exception).substring(0, 1000)
                                        + "\n```")
                                .build()
                        )
                ).build();
    }
    public DiscordMessageDto createMessage(User user) {
        return DiscordMessageDto.builder()
                .content("## 🍀 가입자 수 증가 🥹")
                .embeds(List.of(DiscordEmbedDto.builder()
                                .title("ℹ️ 회원가입 발생")
                                .description("### 🕖 회원가입 발생 시간\n"
                                        + ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
                                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH시 mm분 ss초(서울 시간)"))
                                        + "\n"
                                        + "### 📜 정보 \n"
                                        + "```\n"
                                        + "사용자 닉네임: "+user.getNickName()
                                        + "\n```")
                                .build()
                        )
                ).build();
    }
}
