package com.zhuweiyou.msgbot.platform.wxbot.client;

import org.springframework.http.HttpMethod;

public interface WxbotRequest {
	HttpMethod method();
	String path();
}
