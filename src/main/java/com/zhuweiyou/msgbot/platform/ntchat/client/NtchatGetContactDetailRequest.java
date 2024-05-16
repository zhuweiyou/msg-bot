package com.zhuweiyou.msgbot.platform.ntchat.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NtchatGetContactDetailRequest extends NtchatRequestWithGuid {
	private String wxid;

	@Override
	public String path() {
		return "/contact/get_contact_detail";
	}
}
