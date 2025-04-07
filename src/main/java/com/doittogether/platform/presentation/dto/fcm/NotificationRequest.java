package com.doittogether.platform.presentation.dto.fcm;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

@Schema(description = "푸쉬 알림 요청 메세지 DTO")
public record NotificationRequest(

        @Schema(description = "푸쉬 알림 제목", example = "새로운 메시지가 도착했습니다.")
        String title,

        @Schema(description = "푸쉬 알림 내용", example = "당신에게 새로운 메시지를 보냈습니다. 확인해 보세요.")
        String content,

        @Schema(description = "추가 데이터 (key-value)", example = "{\"data1\": \"data1\", \"data2\": \"data2\"}")
        Map<String, String>data
) {
    public static Message makeMessage(NotificationRequest notificationRequest, String token) {
        Notification notification = Notification.builder()
                .setTitle(notificationRequest.title)
                .setBody(notificationRequest.content)
                .build();
        return Message.builder()
                .setNotification(notification)
                .setToken(token)
                .build();
    }

    public static MulticastMessage makeMulticastMessage(NotificationRequest notificationRequest, List<String> tokens) {
        MulticastMessage.Builder builder = MulticastMessage.builder()
                .addAllTokens(tokens)
                .setNotification(Notification.builder()
                        .setTitle(notificationRequest.title())
                        .setBody(notificationRequest.content())
                        .build());

        if (notificationRequest.data() != null && !notificationRequest.data().isEmpty()) {
            builder.putAllData(notificationRequest.data());
        }

        return builder.build();
    }
}
