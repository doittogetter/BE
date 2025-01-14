package com.doittogether.platform.presentation.dto.fcm;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "푸쉬 알림 요청 DTO")
public record PushRequest(

        @Schema(description = "푸쉬 알림을 받을 디바이스 토큰", example = "device_token_value")
        String token,

        @Schema(description = "푸쉬 알림 제목", example = "Test Notification")
        String title,

        @Schema(description = "푸쉬 알림 내용", example = "This is a test notification")
        String content
) {
    public static Message makeMessage(PushRequest pushRequest) {
        Notification notification = Notification.builder()
                .setTitle(pushRequest.title)
                .setBody(pushRequest.content)
                .build();
        return Message.builder()
                .setNotification(notification)
                .setToken(pushRequest.token)
                .build();
    }
}
