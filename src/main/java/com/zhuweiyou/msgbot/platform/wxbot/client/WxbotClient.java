package com.zhuweiyou.msgbot.platform.wxbot.client;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WxbotClient {
	private String baseUrl;

	public WxbotResponse sendRequest(WxbotRequest request) {
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			HttpPost httpPost = new HttpPost(baseUrl + request.path());
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
			objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
			httpPost.setEntity(new StringEntity(objectMapper.writeValueAsString(request),
				ContentType.APPLICATION_JSON.withCharset(StandardCharsets.UTF_8)));
			try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
				String responseText = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
				return new ObjectMapper().readValue(responseText, WxbotResponse.class);
			}
		} catch (IOException e) {
			WxbotResponse response = new WxbotResponse();
			response.setMsg(e.getMessage());
			return response;
		}
	}
}
