package com.doittogether.platform.business.openai.dto;

import lombok.Data;

@Data
public class AssignChoreChatGPTRequest {
    private String model;
    private String prompt;

    public AssignChoreChatGPTRequest(String model, String prompt){
        this.model = model;
        this.prompt = prompt;
    }
}
