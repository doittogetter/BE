package com.doittogether.platform.presentation.dto.user.response;

import com.doittogether.platform.domain.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "회원 정보 응답")
@Builder
public record UserResponse(
        @Schema(description = "회원 ID")
        Long userId,

        @Schema(description = "닉네임")
        String nickName,

        @Schema(description = "이메일")
        String email,

        @Schema(description = "소셜 ID")
        String socialId,

        @Schema(description = "프로필 이미지 URL")
        String profileImageUrl
) {
    public static UserResponse from(User user) {
        String profileImageUrl = "";
        if (user.getProfileImage() != null) {
            profileImageUrl = user.getProfileImage().getUrl();
        }

        return UserResponse.builder()
                .userId(user.getUserId())
                .nickName(user.getNickName())
                .email(user.getEmail())
                .socialId(user.getSocialId())
                .profileImageUrl(profileImageUrl)
                .build();
    }
}