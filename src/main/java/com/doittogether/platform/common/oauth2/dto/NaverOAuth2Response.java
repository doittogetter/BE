package com.doittogether.platform.common.oauth2.dto;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class NaverOAuth2Response implements OAuth2Response{

    private final Map<String, Object> attributes;

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getProviderId() {
        Map<String, String> response = (Map<String, String>) attributes.get("response");
        return response.get("id");
    }

    @Override
    public String getName() {
        Map<String, String> response = (Map<String, String>) attributes.get("response");
        return response.get("name");
    }

    @Override
    public String getNickname() {
        Map<String, String> response = (Map<String, String>) attributes.get("response");
        return response.get("nickname");
    }

    @Override
    public String getEmail() {
        Map<String, String> response = (Map<String, String>) attributes.get("response");
        return response.get("email");
    }

    @Override
    public String getProfileImage() {
        Map<String, String> response = (Map<String, String>) attributes.get("response");
        return response.get("profile_image");
    }
}
