package com.doittogether.platform.common.oauth2;

import com.doittogether.platform.business.discord.DiscordAlertSender;
import com.doittogether.platform.common.oauth2.dto.*;
import com.doittogether.platform.domain.entity.ProfileImage;
import com.doittogether.platform.domain.entity.User;
import com.doittogether.platform.infrastructure.persistence.user.ProfileImageRepository;
import com.doittogether.platform.infrastructure.persistence.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final ProfileImageRepository profileImageRepository;
    private final Optional<DiscordAlertSender> discordAlertSender;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2Response oAuth2Response = null;
        switch (registrationId) {
            case "kakao":
                oAuth2Response = new KakaoOAuth2Response(oAuth2User.getAttributes());
                break;
            case "google":
                oAuth2Response = new GoogleOAuth2Response(oAuth2User.getAttributes());
                break;
            case "naver":
                oAuth2Response = new NaverOAuth2Response(oAuth2User.getAttributes());
                break;
            default:
                break;
        }

        final String socialId = oAuth2Response.getProvider() + "_" + oAuth2Response.getProviderId();
        final User existedUserOrNull = userRepository.findBySocialId(socialId);
        if (existedUserOrNull == null) {
            final String nickname = oAuth2Response.getNickname();
            final String email = oAuth2Response.getEmail();
            final String profileImageUrl = oAuth2Response.getProfileImage();
            final ProfileImage profileImage = profileImageRepository.saveAndFlush(
                    ProfileImage.from(profileImageUrl)
            );
            final User user = userRepository.save(User.of(
                    nickname,
                    email,
                    socialId,
                    profileImage
            ));
            final OAuth2UserDTO oAuth2UserDTO = OAuth2UserDTO.of(
                    nickname,
                    email,
                    socialId,
                    profileImage
            );
            discordAlertSender.ifPresent(sender -> sender.sendDiscordAlarm(user));
            return new CustomOAuth2User(oAuth2UserDTO);
        }
        final OAuth2UserDTO oAuth2UserDTO = OAuth2UserDTO.of(
                existedUserOrNull.getNickName(),
                existedUserOrNull.getEmail(),
                existedUserOrNull.getSocialId(),
                existedUserOrNull.getProfileImage()
        );
        return new CustomOAuth2User(oAuth2UserDTO);
    }
}
