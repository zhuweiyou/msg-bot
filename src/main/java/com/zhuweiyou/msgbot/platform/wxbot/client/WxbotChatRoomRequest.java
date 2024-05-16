package com.zhuweiyou.msgbot.platform.wxbot.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.web.util.UriComponentsBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WxbotChatRoomRequest implements WxbotRequest {
	private String wxid;

	@Override
	public HttpMethod method() {
		return HttpMethod.GET;
	}

	@Override
	public String path() {
		return UriComponentsBuilder.fromPath("/api/chat-room")
			.queryParam("wxid", wxid)
			.build()
			.toString();
	}
}
