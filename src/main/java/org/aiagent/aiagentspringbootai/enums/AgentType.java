package org.aiagent.aiagentspringbootai.enums;

import lombok.Getter;
import org.aiagent.aiagentspringbootai.constant.PromptConstant;

@Getter
public enum AgentType {
    LOVE_MASTER("loveMaster", PromptConstant.LOVE_MASTER_PROMPT, "恋爱大师"),
    SPRING_AI_EXPERT("springAiExpert", PromptConstant.SPRING_AI_EXPERT_PROMPT, "Spring AI专家");

    private final String code;
    private final String prompt;
    private final String description;

    AgentType(String code, String prompt, String description) {
        this.code = code;
        this.prompt = prompt;
        this.description = description;
    }

    public static AgentType fromCode(String code) {
        for (AgentType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown agent type: " + code);
    }
}
