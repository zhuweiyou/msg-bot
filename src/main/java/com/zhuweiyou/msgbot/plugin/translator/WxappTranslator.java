package com.zhuweiyou.msgbot.plugin.translator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@Component
public class WxappTranslator implements Translator {
	private final WxappConfig wxappConfig;

	@Autowired
	public WxappTranslator(WxappConfig wxappConfig) {
		this.wxappConfig = wxappConfig;
	}

	@Override
	public String getName() {
		return "腾讯翻译君";
	}

	@Override
	public String translate(String input) throws Exception {
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			URI uri = new URIBuilder("https://wxapp.translator.qq.com/api/translate")
				.setParameter("sourceText", input)
				.setParameter("source", "auto")
				.setParameter("target", "auto")
				.setParameter("platform", "WeChat_APP")
				.setParameter("candidateLangs", "zh|en")
				.setParameter("guid", wxappConfig.getGuid())
				.build();
			HttpGet httpGet = new HttpGet(uri);
			httpGet.setHeader("Referer", "https://servicewechat.com/wxb1070eabc6f9107e/118/page-frame.html");
			httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36 MicroMessenger/7.0.20.1781(0x6700143B) NetType/WIFI MiniProgramEnv/Windows WindowsWechat/WMPF WindowsWechat(0x63090819) XWEB/9129");
			try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
				String responseText = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
				JsonNode targetText = new ObjectMapper().readTree(responseText).get("targetText");
				if (targetText == null) {
					throw new Exception("翻译失败");
				}
				return targetText.asText();
			}
		}
	}
}
