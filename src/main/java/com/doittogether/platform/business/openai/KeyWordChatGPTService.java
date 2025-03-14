package com.doittogether.platform.business.openai;

import com.doittogether.platform.business.openai.dto.ChatGPTRequest;
import com.doittogether.platform.business.openai.dto.ChatGPTResponse;
import com.doittogether.platform.business.openai.util.TemplateUtil;
import com.doittogether.platform.presentation.dto.personality.PersonalityRequest;
import com.doittogether.platform.infrastructure.persistence.personality.PersonalityRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class KeyWordChatGPTService {
    private final String model;
    private final String apiURL;
    private final RestTemplate template;

    public KeyWordChatGPTService(
            @Value("${openai.model}") String model,
            @Value("${openai.api.url}") String apiURL,
            RestTemplate template
    ) {
        this.model = model;
        this.apiURL = apiURL;
        this.template = template;
    }

    public ChatGPTResponse chat(final PersonalityRequest request) {
        String personalizedQuestion = null;
        try {
            personalizedQuestion = TemplateUtil.replaceSurveyResultWithJson(KeyWordPrompt.PERSONALITY_PROMPT, request.surveyResultText());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        ChatGPTRequest question = new ChatGPTRequest(model, personalizedQuestion);
        return template.postForObject(apiURL, question, ChatGPTResponse.class);
    }
}
