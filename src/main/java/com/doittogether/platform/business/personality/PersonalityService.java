package com.doittogether.platform.business.personality;

import com.doittogether.platform.domain.entity.User;
import com.doittogether.platform.domain.enumeration.PersonalityStatus;
import com.doittogether.platform.presentation.dto.personality.PersonalityRequest;
import com.doittogether.platform.presentation.dto.personality.PersonalityResponse;

import java.util.List;

public interface PersonalityService {
    PersonalityResponse findKeywordsFromGPT(final User user, final PersonalityRequest request);
    void savePersonalities(final User user, final List<String> keywords, PersonalityStatus status);
    PersonalityResponse getUserPersonalities(User user);
}
