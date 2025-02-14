package com.doittogether.platform.business.user;

import com.doittogether.platform.application.global.code.ExceptionCode;
import com.doittogether.platform.application.global.exception.user.UserException;
import com.doittogether.platform.business.channel.ChannelService;
import com.doittogether.platform.domain.entity.User;
import com.doittogether.platform.domain.entity.UserChannel;
import com.doittogether.platform.infrastructure.persistence.channel.UserChannelRepository;
import com.doittogether.platform.infrastructure.persistence.user.UserRepository;
import com.doittogether.platform.presentation.dto.user.request.UserUpdateRequest;
import com.doittogether.platform.presentation.dto.user.response.UserUpdateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    // 소셜 id의 provider 위치 정보
    private static final int PROVIDER_INDEX = 0;

    private final ChannelService channelService;

    private final UserRepository userRepository;
    private final UserChannelRepository userChannelRepository;

    @Override
    public User findByIdOrThrow(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserException(ExceptionCode.USER_NOT_FOUND));
    }

    @Override
    public boolean hasCompletedSetup(User user) {
        return user.isSetup();
    }

    @Override
    public UserUpdateResponse updateNickname(User user, UserUpdateRequest request) {
        user.updateNickName(request.nickName());

        return UserUpdateResponse.from(user);
    }

    @Override
    public void completeSetup(User user) {
        user.completeSetup();
    }

    @Override
    public String getProvider(String socialId) {

        String provider = socialId.split("_")[PROVIDER_INDEX];

        switch (provider) {
            case "kakao":
                provider = "카카오";
                break;
            case "naver":
                provider = "네이버";
                break;
            case "google":
                provider = "구글";
                break;
            default:
                break;
        }

        return provider;
    }

    @Override
    public void deleteUser(Long userId) {
        User user = findByIdOrThrow(userId);

        List<Long> channelIds = userChannelRepository.findChannelIdsByUser(user);

        channelService.leaveChannels(user, channelIds);

        userRepository.delete(user);
    }
}
