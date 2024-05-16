package com.zhuweiyou.msgbot.platform.ntchat;

import lombok.Data;

import java.util.List;

@Data
public class NtchatWebhookBody {
	private Message message;

	@lombok.Data
	public static class Message {
		private Data data;
	}

	@lombok.Data
	public static class Data {
		private String from_wxid;
		private String msg;
		private String raw_msg;
		private String msgid;
		private String room_wxid;
		private List<String> at_user_list;
	}
}
