package com.zhuweiyou.msgbot.platform.ntchat.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NtchatSendRoomAtRequest extends NtchatRequestWithGuid {
	private String to_wxid;
	private String content;
	private List<String> at_list = new ArrayList<>();

	@Override
	public String path() {
		return "/msg/send_room_at";
	}

	public String getContent() {
		return "{$@}".repeat(at_list.size()) + "\n" + content;
	}
}
