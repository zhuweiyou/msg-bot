package com.zhuweiyou.msgbot.platform.ntchat.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NtchatSetCallbackUrlRequest extends NtchatRequestWithGuid {
	private String callback_url;

	@Override
	public String path() {
		return "/global/set_callback_url";
	}
}
