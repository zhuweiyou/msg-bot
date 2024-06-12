package com.zhuweiyou.msgbot.plugin.countdown;

import com.zhuweiyou.msgbot.platform.Msg;
import com.zhuweiyou.msgbot.platform.Platform;
import com.zhuweiyou.msgbot.plugin.CommandPlugin;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.stream.Collectors;

@Component
public class CountdownPlugin extends CommandPlugin {
	public CountdownPlugin() {
		super("djs", "倒计时");
	}

	@Override
	public void execute(Msg msg, Platform platform) {
		try {
			platform.replyText(msg, getHolidays());
		} catch (Exception e) {
			platform.replyText(msg, e.getMessage());
		}
	}

	private String getHolidays() throws Exception {
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			HttpGet httpGet = new HttpGet("http://www.daojishiqi.com/gengduo.asp");
			try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
				String responseText = EntityUtils.toString(response.getEntity(), Charset.forName("GBK"));
				Document html = Jsoup.parse(responseText);
				Elements dnsq = html.getElementsByClass("dnsq");
				return dnsq.stream().map(item -> {
					String text = item.getElementsByClass("js2").text();
					String day = item.getElementsByClass("js3").text();
					return text + day;
				}).collect(Collectors.joining("\n"));
			}
		}
	}
}
