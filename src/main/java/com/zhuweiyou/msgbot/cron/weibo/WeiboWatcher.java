package com.zhuweiyou.msgbot.cron.weibo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WeiboWatcher {
	private final String userId;
	private final Set<String> bidSet = new HashSet<>();
	private final WeiboFilter weiboFilter;

	public WeiboWatcher(String userId, WeiboFilter weiboFilter) {
		this.userId = userId;
		this.weiboFilter = weiboFilter;
	}

	public Optional<WeiboBlog> getNewBlog() throws Exception {
		List<WeiboBlog> blogList = getBlogList();
		try {
			if (!bidSet.isEmpty() && !blogList.isEmpty()) {
				WeiboBlog firstBlog = blogList.getFirst();
				if (!bidSet.contains(firstBlog.getBid())) {
					return Optional.of(firstBlog);
				}
			}
		} finally {
			bidSet.addAll(blogList.stream().map(WeiboBlog::getBid).toList());
		}
		return Optional.empty();
	}

	public List<WeiboBlog> getBlogList() throws Exception {
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			URI uri = new URIBuilder("https://m.weibo.cn/api/container/getIndex?refer_flag[]=0000015010_&refer_flag[]=0000015010_&from[]=feed&from[]=feed&from[]=feed&loc[]=nickname&loc[]=nickname&loc[]=nickname&is_all[]=1%3Frefer_flag%3D0000015010_&is_all[]=1&is_all[]=1&jumpfrom=weibocom&type=uid")
				.setParameter("value", userId)
				.setParameter("containerid", "107603" + userId)
				.build();
			HttpGet httpGet = new HttpGet(uri);

			try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
				String responseText = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
				JsonNode cards = new ObjectMapper().readTree(responseText).get("data").get("cards");
				List<WeiboBlog> list = new ArrayList<>();
				cards.forEach(card -> {
					try {
						list.add(new ObjectMapper().readValue(card.get("mblog").toString(), WeiboBlog.class));
					} catch (JsonProcessingException ignored) {
					}
				});
				return list.stream()
					.filter(weiboFilter::filter)
					.sorted((a, b) -> b.getCreated_at().compareTo(a.getCreated_at()))
					.toList();
			}
		}
	}
}
