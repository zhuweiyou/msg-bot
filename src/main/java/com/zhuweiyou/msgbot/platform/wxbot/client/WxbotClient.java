package com.zhuweiyou.msgbot.platform.wxbot.client;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WxbotClient {
	private String baseUrl;

	public WxbotResponse sendRequest(WxbotRequest request) {
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			HttpUriRequest httpUriRequest;
			if (request.method() == HttpMethod.GET) {
				ObjectMapper objectMapper = new ObjectMapper();
				objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
				URI uri = UriComponentsBuilder.newInstance()
					.uri(new URIBuilder(baseUrl + request.path()).build())
					.queryParams((MultiValueMap<String, String>) objectMapper.convertValue(request, Map.class))
					.build()
					.toUri();
				httpUriRequest = new HttpGet(uri);
			} else if (request.method() == HttpMethod.POST) {
				HttpPost httpPost = new HttpPost(baseUrl + request.path());
				ObjectMapper objectMapper = new ObjectMapper();
				objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
				objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
				httpPost.setEntity(new StringEntity(objectMapper.writeValueAsString(request),
					ContentType.APPLICATION_JSON.withCharset(StandardCharsets.UTF_8)));
				httpUriRequest = httpPost;
			} else {
				throw new Exception("不支持" + request.method().toString() + "方法");
			}
			try (CloseableHttpResponse response = httpClient.execute(httpUriRequest)) {
				String responseText = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
				return new ObjectMapper().readValue(responseText, WxbotResponse.class);
			}
		} catch (Exception e) {
			WxbotResponse response = new WxbotResponse();
			response.setMsg(e.getMessage());
			return response;
		}
	}
}
