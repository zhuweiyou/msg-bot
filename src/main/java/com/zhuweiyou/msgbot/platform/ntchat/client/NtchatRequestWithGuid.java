package com.zhuweiyou.msgbot.platform.ntchat.client;

import lombok.Data;

@Data
public abstract class NtchatRequestWithGuid implements NtchatRequest {
	private String guid;
}
