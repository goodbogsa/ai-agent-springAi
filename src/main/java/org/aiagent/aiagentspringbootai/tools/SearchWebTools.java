package org.aiagent.aiagentspringbootai.tools;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class SearchWebTools {

    private static final String SEARCH_API_URL = "https://www.searchapi.io/api/v1/search";
    private static final String ENGINE = "baidu";

    private final String apiKey;
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SearchWebTools(String apiKey) {
        this.apiKey = apiKey;
    }

    @Tool(description = "Search the web using Baidu via SearchAPI. Returns organic search results with title and link.")
    public String searchWeb(@ToolParam(description = "The search query string") String query) {
        try {
            HttpUrl.Builder urlBuilder = HttpUrl.get(SEARCH_API_URL).newBuilder();
            urlBuilder.addQueryParameter("engine", ENGINE);
            urlBuilder.addQueryParameter("q", query);
            urlBuilder.addQueryParameter("api_key", apiKey);

            Request request = new Request.Builder()
                    .url(urlBuilder.build())
                    .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                return "搜索请求失败：" + response.code();
            }

            String responseBody = response.body().string();
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode organicResults = rootNode.get("organic_results");

            if (organicResults == null || organicResults.isEmpty()) {
                return "未找到搜索结果";
            }

            List<SearchResult> results = new ArrayList<>();
            for (JsonNode result : organicResults) {
                SearchResult searchResult = new SearchResult();
                searchResult.title = result.get("title").asText("");
                searchResult.link = result.get("link").asText("");
                results.add(searchResult);
            }

            StringBuilder sb = new StringBuilder();
            sb.append("搜索结果：\n");
            for (int i = 0; i < Math.min(5, results.size()); i++) {
                SearchResult r = results.get(i);
                sb.append(String.format("%d. %s\n   %s\n\n",
                        i + 1, r.title, r.link));
            }

            return sb.toString();

        } catch (IOException e) {
            return "搜索失败：" + e.getMessage();
        }
    }

    private static class SearchResult {
        String title;
        String link;
    }
}


