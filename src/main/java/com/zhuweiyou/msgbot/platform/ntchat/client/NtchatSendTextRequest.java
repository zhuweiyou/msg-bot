package com.zhuweiyou.msgbot.platform.ntchat.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NtchatSendTextRequest extends NtchatRequestWithGuid {
	private String to_wxid;
	private String content;

	@Override
	public String path() {
		return "/msg/send_text";
	}
}
