package org.aiagent.aiagentspringbootai.config;

import org.aiagent.aiagentspringbootai.tools.*;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 统一配置ai工具类
 */
@Configuration
public class AiToolsConfig {
    @Value("${searchapi.search-key}")
    private String API_KEY;

    @Bean
    public ToolCallback[] getAallTools() {
        return ToolCallbacks.from(
                new FileOperationTool(),
                new ResourceDownloadTool(),
                new SearchWebTools(API_KEY),
                new WebScrapingTool(),
                new PDFGenerationTool());
    }
}
