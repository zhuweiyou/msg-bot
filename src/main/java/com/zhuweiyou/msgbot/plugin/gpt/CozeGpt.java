package com.zhuweiyou.msgbot.plugin.gpt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
	public String prompt(String input) throws Exception {
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			String[] nextApiKey = cozeConfig.getNextApiKey().split(":");
			if (nextApiKey.length != 2) {
				throw new Exception("token配置错误");
			}

			Map<String, Object> body = new HashMap<>();
			body.put("bot_id", nextApiKey[0]);
			body.put("user", "用户1");
			body.put("query", input);
			body.put("stream", false);

			HttpPost httpPost = new HttpPost("https://api.coze.cn/open_api/v2/chat");
			httpPost.setHeader("Authorization", "Bearer " + nextApiKey[1]);
			httpPost.setEntity(new StringEntity(new ObjectMapper().writeValueAsString(body),
				ContentType.APPLICATION_JSON.withCharset(StandardCharsets.UTF_8)));
			try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
				String responseText = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
				JsonNode content = new ObjectMapper().readTree(responseText).path("messages").path(0).get("content");
				if (content == null) {
					throw new Exception("请求失败");
				}
				return content.asText();
			}
		}
	}
}
