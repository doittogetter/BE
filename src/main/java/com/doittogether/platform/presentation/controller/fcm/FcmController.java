package com.doittogether.platform.presentation.controller.fcm;

import com.doittogether.platform.application.global.code.SuccessCode;
import com.doittogether.platform.application.global.response.SuccessResponse;
import com.doittogether.platform.business.fcm.FcmService;
import com.doittogether.platform.business.user.UserService;
import com.doittogether.platform.domain.entity.User;
import com.doittogether.platform.presentation.dto.fcm.SaveOrUpdateTokenRequest;
import com.doittogether.platform.presentation.dto.reaction.ReactionRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/fcms")
@RequiredArgsConstructor
@Tag(name = "FCM API", description = "FCM 토큰 및 푸쉬 알림 관리 API")
public class FcmController {

    private final FcmService fcmService;
    private final UserService userService;

    @Operation(summary = "FCM 토큰 저장 또는 업데이트", description = "로그인한 사용자의 FCM 토큰을 저장하거나 업데이트합니다.")
    @PostMapping("/token")
    public ResponseEntity<SuccessResponse<Void>> saveOrUpdateToken(
            Principal principal,
            @RequestBody SaveOrUpdateTokenRequest saveOrUpdateTokenRequest) {

        Long userId = Long.parseLong(principal.getName());
        User user = userService.findByIdOrThrow(userId);

        fcmService.saveOrUpdateToken(user, saveOrUpdateTokenRequest);

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.onSuccess(SuccessCode._OK, null));
    }

    @Operation(summary = "푸쉬 알림 전송", description = "타켓 사용자에게 푸쉬 알림을 전송합니다.")
    @GetMapping("/push")
    public ResponseEntity<SuccessResponse<Void>> sendPushNotification(
            Principal principal,
            @RequestBody ReactionRequest reactionRequest) {

        Long userId = Long.parseLong(principal.getName());
        User user = userService.findByIdOrThrow(userId);

        fcmService.sendNotification(user, reactionRequest);

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.onSuccess(SuccessCode._OK, null));
    }
}
