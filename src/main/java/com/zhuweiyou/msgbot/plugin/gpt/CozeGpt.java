package com.zhuweiyou.msgbot.plugin.gpt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class CozeGpt implements Gpt {
	private final CozeConfig cozeConfig;

	@Autowired
	public CozeGpt(CozeConfig cozeConfig) {
		this.cozeConfig = cozeConfig;
	}

	@Override
	public String getName() {
		return "coze";
	}

	@Override
	public String prompt(String input, boolean fuck) throws Exception {
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			CozeBot cozeBot = fuck ? cozeConfig.getFuckBot() : cozeConfig.getNormalBot();
			Map<String, Object> body = new HashMap<>();
			body.put("bot_id", cozeBot.getBotId());
			body.put("user", "用户1");
			body.put("query", input);
			body.put("stream", false);
			HttpPost httpPost = new HttpPost("https://api.coze.cn/open_api/v2/chat");
			httpPost.setHeader("Authorization", "Bearer " + cozeBot.getAccessToken());
			httpPost.setEntity(new StringEntity(new ObjectMapper().writeValueAsString(body),
				ContentType.APPLICATION_JSON.withCharset(StandardCharsets.UTF_8)));
			try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
				String responseText = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
				String content = new ObjectMapper().readTree(responseText).path("messages").path(0).path("content").asText();
				if (Strings.isBlank(content)) {
					throw new Exception("请求失败");
				}
				return content;
			}
		}
	}
}
