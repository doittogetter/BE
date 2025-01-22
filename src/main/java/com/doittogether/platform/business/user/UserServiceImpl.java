package com.doittogether.platform.business.user;

import com.doittogether.platform.application.global.code.ExceptionCode;
import com.doittogether.platform.application.global.exception.user.UserException;
import com.doittogether.platform.domain.entity.User;
import com.doittogether.platform.infrastructure.persistence.user.UserRepository;
import com.doittogether.platform.presentation.dto.user.request.UserUpdateRequest;
import com.doittogether.platform.presentation.dto.user.response.UserUpdateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    // 소셜 id의 provider 위치 정보
    private static final int PROVIDER_INDEX = 0;

    private final UserRepository userRepository;

    @Override
    public User findByIdOrThrow(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserException(ExceptionCode.USER_NOT_FOUND));
    }

    @Override
    public boolean isSetup(User user) {
        return user.isSetup();
    }

    @Override
    public UserUpdateResponse updateNickname(User user, UserUpdateRequest request) {
        user.updateNickName(request.nickName());
        user = userRepository.save(user);

        return UserUpdateResponse.from(user);
    }

    @Override
    public void completeSetup(User user) {
        user.completeSetup();
        userRepository.save(user);
    }

    @Override
    public String getProvider(String socialId){

        String provider = socialId.split("_")[PROVIDER_INDEX];

        return provider;
    }
}
