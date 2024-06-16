package com.zhuweiyou.msgbot.plugin.translator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
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
import java.util.stream.Collectors;

@Component
public class BaiduTranslator implements Translator {
	private final BaiduConfig baiduConfig;

	@Autowired
	public BaiduTranslator(BaiduConfig baiduConfig) {
		this.baiduConfig = baiduConfig;
	}

	@Override
	public String getName() {
		return "百度翻译";
	}

	@Override
	public String translate(String input) throws Exception {
		String salt = System.currentTimeMillis() + "";
		String sign = DigestUtils.md5Hex(String.join("", baiduConfig.getAppId(), input, salt, baiduConfig.getAppKey()));

		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			URI uri = new URIBuilder("http://api.fanyi.baidu.com/api/trans/vip/translate")
				.setParameter("q", input)
				.setParameter("appid", baiduConfig.getAppId())
				.setParameter("salt", salt)
				.setParameter("from", "auto")
				.setParameter("to", "auto")
				.setParameter("sign", sign)
				.build();
			HttpGet httpGet = new HttpGet(uri);

			try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
				String responseText = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
				BaiduResponse baiduResponse = new ObjectMapper().readValue(responseText, BaiduResponse.class);
				if (!baiduResponse.isSuccess()) {
					throw new Exception(baiduResponse.getError_msg());
				}

				return baiduResponse.getTrans_result().stream().map(BaiduResponse.TransResult::getDst).collect(Collectors.joining());
			}
		}
	}
}
