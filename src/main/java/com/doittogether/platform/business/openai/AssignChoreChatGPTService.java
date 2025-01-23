package com.doittogether.platform.business.openai;

import com.doittogether.platform.business.openai.dto.AssignChoreChatGPTRequest;
import com.doittogether.platform.business.openai.dto.AssignChoreChatGPTResponse;
import com.doittogether.platform.business.openai.util.TemplateUtil;
import com.doittogether.platform.domain.entity.Housework;
import com.doittogether.platform.domain.entity.Personality;
import com.doittogether.platform.domain.entity.User;
import com.doittogether.platform.infrastructure.persistence.housework.HouseworkRepository;
import com.doittogether.platform.infrastructure.persistence.personality.PersonalityRepository;
import com.doittogether.platform.infrastructure.persistence.user.UserRepository;
import com.doittogether.platform.presentation.dto.housework.HouseworkUserRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssignChoreChatGPTService {
    private final PersonalityRepository personalityRepository;
    private final UserRepository userRepository;
    private final HouseworkRepository houseworkRepository;
    private final RestTemplate template;

    @Value("${openai.model}")
    private String model;
    @Value("${openai.api.url}")
    private String apiURL;

    public Map<Long, List<String>> findUserPersonality(final HouseworkUserRequest channelUserRequest) {
        final List<User> channelUsers = userRepository.findByChannelId(channelUserRequest.channelId());

        final Map<Long, List<String>> userPersonality = channelUsers.stream()
                .collect(Collectors.toMap(
                        User::retrieveUserId,
                        user -> personalityRepository.findByUser(user)
                                .stream()
                                .map(Personality::retrieveValue)
                                .collect(Collectors.toList())
                ));

        return userPersonality;
    }

    //userchannelId, houseworkId, nameOfHousework
    public AssignChoreChatGPTResponse chat(final HouseworkUserRequest channelUserRequest) {
        final Map<Long, List<String>> userPersonality = findUserPersonality(channelUserRequest);
        final Optional<Housework> housework = houseworkRepository.findByChannelChannelIdAndHouseworkId(channelUserRequest.channelId(),
                channelUserRequest.houseworkId());

        String assignProperHouswork = null;
        try {
            assignProperHouswork = TemplateUtil.replaceUserPersonalityWithJson(AssignChorePrompt.ASSIGN_CHORES_PROMPT,
                    userPersonality, housework.map(Housework::retrieveTask)
                            .orElseThrow(() -> new RuntimeException("Housework is not present")));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        AssignChoreChatGPTRequest question = new AssignChoreChatGPTRequest(model, assignProperHouswork);
        return template.postForObject(apiURL, question, AssignChoreChatGPTResponse.class);
    }
}
