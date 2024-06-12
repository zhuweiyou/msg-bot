package com.zhuweiyou.msgbot.plugin.gpt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhuweiyou.msgbot.common.AppConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MoonshotGpt implements Gpt {
	private final AppConfig appConfig;
	private final MoonshotConfig moonshotConfig;

	@Autowired
	public MoonshotGpt(MoonshotConfig moonshotConfig, AppConfig appConfig) {
		this.moonshotConfig = moonshotConfig;
		this.appConfig = appConfig;
	}

	@Override
	public String getName() {
		return "moonshot";
	}

	@Override
	public String prompt(String input) throws Exception {
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			Map<String, Object> body = new HashMap<>();
			body.put("model", "moonshot-v1-8k");
			body.put("temperature", 0.5);

			List<Map<String, Object>> messages = new ArrayList<>();
			// Map<String, Object> system = new HashMap<>();
			// system.put("role", "system");
			// system.put("content", "");
			// messages.add(system);

			Map<String, Object> user = new HashMap<>();
			user.put("role", "user");
			user.put("content", input);
			messages.add(user);

			body.put("messages", messages);

			HttpPost httpPost = new HttpPost("https://api.moonshot.cn/v1/chat/completions");
			httpPost.setHeader("Authorization", "Bearer " + moonshotConfig.getNextApiKey());
			httpPost.setEntity(new StringEntity(new ObjectMapper().writeValueAsString(body),
				ContentType.APPLICATION_JSON.withCharset(StandardCharsets.UTF_8)));

			try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
				String responseText = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
				if (responseText.contains("_error\"")) {
					throw new Exception("请求失败");
				}

				JsonNode content = new ObjectMapper().readTree(responseText).path("choices").path(0).path("message").get("content");
				if (content == null) {
					throw new Exception("请求失败");
				}
				return content.asText().replaceAll("(?i)MoonshotAI", appConfig.getBotName())
					.replaceAll("(?i)Moonshot Corp", "zhu");
			}
		}
	}
}
