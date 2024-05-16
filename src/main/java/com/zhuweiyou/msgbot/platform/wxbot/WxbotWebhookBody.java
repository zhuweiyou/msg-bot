package com.zhuweiyou.msgbot.platform.wxbot;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.apache.logging.log4j.util.Strings;

import java.util.List;

@Data
public class WxbotWebhookBody {
	private List<Data> data;

	@lombok.Data
	public static class Data {
		@JsonProperty("MsgSvrID")
		private String MsgSvrID;
		@JsonProperty("StrContent")
		private String StrContent;
		// 群ID 或 用户ID
		@JsonProperty("StrTalker")
		private String StrTalker;
		// 当是群聊的时候 才会有这个用户ID
		@JsonProperty("Sender")
		private String Sender;

		public boolean isSelf() {
			if (Strings.isBlank(Sender)) {
				return false;
			}
			return Sender.startsWith("<msgsource>");
		}

		public boolean isXmlContent() {
			if (Strings.isBlank(StrContent)) {
				return false;
			}
			return StrContent.startsWith("<?xml version=\"1.0\"?>");
		}

		public boolean isGroup() {
			if (Strings.isBlank(StrTalker)) {
				return false;
			}
			return StrTalker.contains("@chatroom");
		}
	}
}
