package com.zhuweiyou.msgbot.platform.ntchat.client;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class NtchatSendDatImageRequest extends NtchatRequestWithImage {

	@Override
	public String path() {
		return "/msg/send_dat_image";
	}
}
