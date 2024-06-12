package com.zhuweiyou.msgbot.cron.weibo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeiboBlog {
	private String bid;
	private String text;
	@JsonFormat(pattern = "EEE MMM dd HH:mm:ss Z yyyy", locale = "en_US")
	private Date created_at;
	private User user;

	@Override
	public String toString() {
		return String.format("%s\nhttps://weibo.com/%s/%s",
			text.replaceAll("</?.*?>", "").replace("&amp;", "&").trim(),
			user.getId(),
			bid
		);
	}

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class User {
		private String id;
	}
}
