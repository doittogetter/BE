package com.doittogether.platform.business.openai.dto;

import lombok.Data;

@Data
public class AssignChoreChatGPTResponse {
    private Long userId;
    private String Assignee;
}
