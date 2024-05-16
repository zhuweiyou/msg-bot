package com.zhuweiyou.msgbot.platform.ntchat.client;

import lombok.Data;

@Data
public class NtchatCreateClientRequest implements NtchatRequest {

	@Override
	public String path() {
		return "/client/create";
	}
}
