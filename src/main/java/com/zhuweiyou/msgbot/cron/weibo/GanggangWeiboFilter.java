package com.zhuweiyou.msgbot.cron.weibo;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class GanggangWeiboFilter implements WeiboFilter {
	private final static Pattern FILTER_PATTERN = Pattern.compile("刚刚，本人|生日|新闻联播|关注我了|闹钟响了|我累了|站出来了|流了一身汗|了一番|我的|粉丝福利|抽奖|盗墓笔记|刚刚，我");

	@Override
	public boolean filter(WeiboBlog blog) {
		return blog.getText().startsWith("刚刚") && !FILTER_PATTERN.matcher(blog.getText()).find();
	}
}
