package org.aiagent.aiagentspringbootai.app;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aiagent.aiagentspringbootai.advisor.AiLoggerAdvisor;
import org.aiagent.aiagentspringbootai.enums.AgentType;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.aiagent.aiagentspringbootai.constant.AgentConstant.MEMORY_AGENT_APPID;
import static org.aiagent.aiagentspringbootai.constant.AgentConstant.MEMORY_AGENT_NUMBER;

@Slf4j
@Component
public class AgentAiService {
    @Resource
    private AgentAppFactory agentAppFactory;

    @Resource
    private ToolCallback[] toolCallbacks;


//    record ChatQuestion(String question, String answer){}
    public String doChat(String message, String appId) {
        ChatClient chatClient = agentAppFactory.createChatClient(AgentType.SPRING_AI_EXPERT);
        ChatResponse response = chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(MEMORY_AGENT_APPID, appId).param(MEMORY_AGENT_NUMBER, "10"))
                .advisors(new AiLoggerAdvisor())
                .call()
//                .entity(ChatQuestion.class);
                .chatResponse();
//        log.info("result: {}", response.question + ": " + response.answer);
        return response.getResult().getOutput().getText(); // Changed from return response;
    }

    public String doChatWithTools(String message, String appId) {
        ChatClient chatClient = agentAppFactory.createChatClient(AgentType.SPRING_AI_EXPERT);
        ChatResponse response = chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(MEMORY_AGENT_APPID, appId).param(MEMORY_AGENT_NUMBER, "10"))
                .advisors(new AiLoggerAdvisor())
                .toolCallbacks(toolCallbacks)
                .call()
//                .entity(ChatQuestion.class);
                .chatResponse();
//        log.info("result: {}", response.question + ": " + response.answer);
        return response.getResult().getOutput().getText(); // Changed from return response;
    }
}
