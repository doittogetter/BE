package com.doittogether.platform.business.openai.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AssignChoreChatGPTRequest {
    private String model;
    private List<Message> messages;

    public AssignChoreChatGPTRequest(String model, String prompt) {
        this.model = model;
        this.messages =  new ArrayList<>();
        this.messages.add(new Message("user", prompt));
    }
}
