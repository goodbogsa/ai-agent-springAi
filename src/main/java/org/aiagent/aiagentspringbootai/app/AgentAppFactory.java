package org.aiagent.aiagentspringbootai.app;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aiagent.aiagentspringbootai.enums.AgentType;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;


import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.aiagent.aiagentspringbootai.constant.AgentConstant.MEMORY_AGENT_APPID;
import static org.aiagent.aiagentspringbootai.constant.AgentConstant.MEMORY_AGENT_NUMBER;
import static org.aiagent.aiagentspringbootai.constant.PromptConstant.LOVE_MASTER_PROMPT;
import static org.aiagent.aiagentspringbootai.constant.PromptConstant.SPRING_AI_EXPERT_PROMPT;

@Slf4j
@Configuration
public class AgentAppFactory {

    @Resource
    private ChatModel dashscopeChatModel;

    //TODO可改为caffine缓存
    private final Map<String, ChatClient> chatClientCache = new ConcurrentHashMap<>();

    public ChatClient getChatClient(ChatModel chatModel, AgentType agentType) {
        return chatClientCache.computeIfAbsent(agentType.getCode(),
                key -> createChatClient(agentType));
    }
    public ChatClient createChatClient(AgentType agentType) {
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .build();
        ChatClient chatClient = createChatClient(chatMemory,null, agentType.getPrompt());
        return chatClient;
    }

    public ChatClient createChatClient( ChatMemory chatMemory, List<Advisor> advisors, String prompt) {
        //构建默认的ChatClient
        ChatClient.Builder builder = ChatClient.builder(dashscopeChatModel);
        if (prompt != null && !prompt.isEmpty()) {
            builder.defaultSystem(prompt);
        }
        // 如果提供了聊天记忆，则添加记忆功能
        if (chatMemory != null) {
            builder.defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build());
        }
        // 如果提供了自定义顾问列表，则添加
        if (advisors != null && !advisors.isEmpty()) {
            builder.defaultAdvisors(advisors.toArray(new Advisor[0]));
        }
        return builder.build();
    }
}
