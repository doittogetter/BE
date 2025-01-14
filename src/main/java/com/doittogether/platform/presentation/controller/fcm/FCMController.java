package com.doittogether.platform.presentation.controller.fcm;

import com.doittogether.platform.application.global.code.SuccessCode;
import com.doittogether.platform.application.global.response.SuccessResponse;
import com.doittogether.platform.business.fcm.FCMService;
import com.doittogether.platform.presentation.dto.fcm.PushRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/push")
@RequiredArgsConstructor
public class FCMController {

    private final FCMService fcmService;

    @GetMapping("/test-fcm")
    public ResponseEntity<SuccessResponse<Void>> sendPushNotification(
            @RequestBody PushRequest pushRequest) {

        // 푸쉬 알림 처리 로직
        System.out.println("Token: " + pushRequest.token());
        System.out.println("Title: " + pushRequest.title());
        System.out.println("Content: " + pushRequest.content());

        fcmService.sendNotification(pushRequest);

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.onSuccess(SuccessCode._OK, null));
    }
}
