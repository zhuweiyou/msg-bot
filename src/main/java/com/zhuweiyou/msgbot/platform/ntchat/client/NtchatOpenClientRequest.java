package com.zhuweiyou.msgbot.platform.ntchat.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NtchatOpenClientRequest extends NtchatRequestWithGuid {
	private boolean smart;

	@Override
	public String path() {
		return "/client/open";
	}
}
