package com.zhuweiyou.msgbot.cron.bilibili;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BilibiliCard {
	private String aid;
	private String title;
	private Long ctime;
	private Owner owner;
	private String bvid;

	@Override
	public String toString() {
		return String.join("\n", String.format("【哔哩哔哩】%s", owner.getName()), title, "https://b23.tv/" + bvid);
	}

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Owner {
		private String name;
	}
}
