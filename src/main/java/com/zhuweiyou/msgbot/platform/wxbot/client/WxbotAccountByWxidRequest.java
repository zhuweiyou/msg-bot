package com.zhuweiyou.msgbot.platform.wxbot.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpMethod;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WxbotAccountByWxidRequest implements WxbotRequest {
	private String wxid;

	@Override
	public HttpMethod method() {
		return HttpMethod.GET;
	}

	@Override
	public String path() {
		return "/api/account-by-wxid";
	}
}
