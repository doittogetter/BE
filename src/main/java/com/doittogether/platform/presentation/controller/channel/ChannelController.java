package com.doittogether.platform.presentation.controller.channel;

import com.doittogether.platform.application.global.code.SuccessCode;
import com.doittogether.platform.application.global.response.SuccessResponse;
import com.doittogether.platform.business.channel.ChannelService;
import com.doittogether.platform.business.user.UserService;
import com.doittogether.platform.domain.entity.User;
import com.doittogether.platform.presentation.dto.channel.request.ChannelInviteLinkTestRequest;
import com.doittogether.platform.presentation.dto.channel.request.ChannelKickUserRequest;
import com.doittogether.platform.presentation.dto.channel.request.ChannelRegisterRequest;
import com.doittogether.platform.presentation.dto.channel.request.ChannelUpdateRequest;
import com.doittogether.platform.presentation.dto.channel.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/channels")
@RequiredArgsConstructor
@Tag(name = "채널 API", description = "채널 관리 API")
public class ChannelController {

    private final UserService userService;
    private final ChannelService channelService;

    @GetMapping("/my")
    @Operation(summary = "나의 채널 목록 조회", description = "내가 속한 채널 목록을 조회합니다.")
    public ResponseEntity<SuccessResponse<ChannelListResponse>> getChannel(
            Principal principal,
            @ParameterObject Pageable pageable) {

        Long userId = Long.parseLong(principal.getName());
        User user = userService.findByIdOrThrow(userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                SuccessResponse.onSuccess(
                        SuccessCode._OK,
                        channelService.getMyChannels(user, pageable)
                ));
    }

    @PostMapping
    @Operation(summary = "채널 생성", description = "관리자 유저가 새로운 채널을 생성합니다.")
    public ResponseEntity<SuccessResponse<ChannelRegisterResponse>> createChannel(
            Principal principal,
            @RequestBody @Valid ChannelRegisterRequest request) {

        Long userId = Long.parseLong(principal.getName());
        User user = userService.findByIdOrThrow(userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                SuccessResponse.onSuccess(
                        SuccessCode._OK,
                        channelService.createChannel(user, request)
                ));
    }

    @PutMapping("/{channelId}/name")
    @Operation(summary = "채널명 변경", description = "관리자 유저가 채널명을 변경합니다.")
    public ResponseEntity<SuccessResponse<ChannelUpdateResponse>> updateChannelName(
            Principal principal,
            @PathVariable("channelId") Long channelId, @RequestBody @Valid ChannelUpdateRequest request) {

        Long userId = Long.parseLong(principal.getName());
        User user = userService.findByIdOrThrow(userId);

        return ResponseEntity.status(HttpStatus.OK).body(
                SuccessResponse.onSuccess(
                        SuccessCode._OK,
                        channelService.updateChannelName(user, channelId, request)
                ));
    }

    @GetMapping("/{channelId}/users")
    @Operation(summary = "채널 사용자 조회", description = "채널에 포함된 모든 사용자를 조회합니다.")
    public ResponseEntity<SuccessResponse<ChannelUserListResponse>> getChannelUsers(
            Principal principal,
            @PathVariable("channelId") Long channelId,
            @ParameterObject Pageable pageable) {

        Long userId = Long.parseLong(principal.getName());
        User user = userService.findByIdOrThrow(userId);

        return ResponseEntity.status(HttpStatus.OK).body(
                SuccessResponse.onSuccess(
                        SuccessCode._OK,
                        channelService.getChannelUsers(user, channelId, pageable)
                ));
    }

    @PostMapping("/{channelId}/invite-link")
    @Operation(summary = "초대 링크 생성", description = "특정 채널에 대한 초대 링크를 생성합니다.")
    public ResponseEntity<SuccessResponse<ChannelInviteLinkResponse>> generateInviteLink(
            @PathVariable("channelId") Long channelId) {

        return ResponseEntity.status(HttpStatus.CREATED).body(
                SuccessResponse.onSuccess(
                        SuccessCode._OK,
                        channelService.generateInviteLink(channelId)
                ));
    }

    @PostMapping("/{channelId}/invite-link-test") // 비공개 테스트 api
    public ResponseEntity<SuccessResponse<ChannelInviteLinkResponse>> generateInviteLinkTest(
            @PathVariable("channelId") Long channelId,
            @RequestBody ChannelInviteLinkTestRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(
                SuccessResponse.onSuccess(
                        SuccessCode._OK,
                        channelService.generateInviteLinkTest(channelId, request)
                ));
    }

    @PostMapping("/join/{inviteLink}")
    @Operation(summary = "초대 링크로 방 입장", description = "초대 링크를 통해 채널에 입장합니다.")
    public ResponseEntity<SuccessResponse<ChannelJoinResponse>> joinChannelViaInviteLink(
            Principal principal,
            @PathVariable("inviteLink") String inviteLink) {

        Long userId = Long.parseLong(principal.getName());
        User user = userService.findByIdOrThrow(userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                SuccessResponse.onSuccess(
                        SuccessCode._OK,
                        channelService.joinChannelViaInviteLink(user, inviteLink)
                ));
    }

    @PostMapping("/{channelId}/kick")
    @Operation(summary = "특정 유저 추방", description = "특정 유저를 채널에서 강퇴합니다.")
    public ResponseEntity<SuccessResponse<ChannelKickUserResponse>> kickUserFromChannel(
            Principal principal,
            @PathVariable("channelId") Long channelId, @Valid @RequestBody ChannelKickUserRequest request) {

        Long userId = Long.parseLong(principal.getName());
        User user = userService.findByIdOrThrow(userId);

        return ResponseEntity.status(HttpStatus.OK).body(
                SuccessResponse.onSuccess(
                        SuccessCode._OK,
                        channelService.kickUserFromChannel(user, channelId, request)
                ));
    }

    @DeleteMapping("/{channelId}/leave")
    @Operation(summary = "일반 참가자가 채널 나가기", description = "일반 참가자가 채널을 나가면 연관된 데이터가 삭제됩니다.")
    public ResponseEntity<SuccessResponse<Void>> leaveChannel(
            Principal principal,
            @PathVariable("channelId") Long channelId) {

        Long userId = Long.parseLong(principal.getName());
        User user = userService.findByIdOrThrow(userId);

        channelService.leaveChannels(user, channelId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.onSuccess(SuccessCode._OK, null));
    }
}
