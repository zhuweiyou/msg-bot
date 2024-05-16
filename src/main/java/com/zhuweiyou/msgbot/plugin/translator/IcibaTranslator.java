package com.zhuweiyou.msgbot.plugin.translator;

import com.zhuweiyou.msgbot.common.StringUtil;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@Component
public class IcibaTranslator implements Translator {
	@Override
	public String getName() {
		return "爱词霸";
	}

	@Override
	public String translate(String input) throws Exception {
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			URI uri = new URIBuilder("https://open.iciba.com/huaci_v3/dict.php")
				.setParameter("word", input.replaceAll("'", "&apos;"))
				.build();
			HttpGet httpGet = new HttpGet(uri);

			try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
				String responseText = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
				responseText = StringUtil.getMiddle(responseText, "dict.innerHTML='", "';\n");
				responseText = Jsoup.parse(responseText).text();
				responseText = responseText.replaceFirst("详细释义$", "");
				responseText = responseText.replaceAll("[ |\\t]+", " ");
				responseText = responseText.replace("当前选中内容暂无解释！ 建议您： 去爱词霸官网翻译 百度搜索一下", "");
				responseText = responseText.replaceAll("\\\\", "");
				return responseText;
			}
		}
	}
}
