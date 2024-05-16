package com.zhuweiyou.msgbot.cron.bilibili;

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
import java.util.stream.Collectors;

public class BilibiliWatcher {
	private final String userId;
	private final Set<String> aidSet = new HashSet<>();

	public BilibiliWatcher(String userId) {
		this.userId = userId;
	}

	public Optional<BilibiliCard> getNewCard() throws Exception {
		List<BilibiliCard> cardList = getCardList();
		try {
			if (!aidSet.isEmpty() && !cardList.isEmpty()) {
				BilibiliCard firstCard = cardList.getFirst();
				if (!aidSet.contains(firstCard.getAid())) {
					return Optional.of(firstCard);
				}
			}
		} finally {
			aidSet.addAll(cardList.stream().map(BilibiliCard::getAid).toList());
		}
		return Optional.empty();
	}

	public List<BilibiliCard> getCardList() throws Exception {
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			URI uri = new URIBuilder("https://api.vc.bilibili.com/dynamic_svr/v1/dynamic_svr/space_history?need_top=1")
				.setParameter("host_uid", userId)
				.build();
			HttpGet httpGet = new HttpGet(uri);
			httpGet.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36");
			httpGet.setHeader("Referer", "https://m.bilibili.com/");

			try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
				String responseText = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
				JsonNode responseJson = new ObjectMapper().readTree(responseText);
				List<BilibiliCard> list = new ArrayList<>();
				responseJson.get("data").get("cards").forEach(card -> {
					try {
						BilibiliCard bilibiliCard = new ObjectMapper().readValue(card.get("card").asText(), BilibiliCard.class);
						bilibiliCard.setBvid(card.get("desc").get("bvid").asText());
						list.add(bilibiliCard);
					} catch (JsonProcessingException ignored) {
					}
				});
				return list.stream()
					.filter(item -> item.getCtime() != null)
					.sorted((a, b) -> b.getCtime().compareTo(a.getCtime()))
					.collect(Collectors.toList());
			}
		}
	}
}
