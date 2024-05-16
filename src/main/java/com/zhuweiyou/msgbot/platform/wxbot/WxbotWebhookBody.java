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
		// 文本 / 图片 / 其它
		@JsonProperty("StrContent")
		private String StrContent;
		// 引用 / 订阅
		@JsonProperty("Content")
		private String Content;
		// 群ID 或 用户ID
		@JsonProperty("StrTalker")
		private String StrTalker;
		// 当是群聊的时候 才会有这个用户ID
		@JsonProperty("Sender")
		private String Sender;

		public String getText() {
			if (Strings.isNotBlank(getRaw())) {
				return "";
			}
			if (Strings.isNotBlank(StrContent)) {
				return StrContent;
			}
			return Content;
		}

		public String getRaw() {
			if (Strings.isNotBlank(Content)) {
				return Content;
			}
			if (Strings.isBlank(StrContent)) {
				return "";
			}
			if (StrContent.startsWith("<?xml version=\"1.0\"?>")) {
				return StrContent;
			}
			return "";
		}

		public boolean isSelf() {
			if (Strings.isBlank(Sender)) {
				return false;
			}
			return Sender.startsWith("<msgsource>") || Sender.length() >= 32;
		}

		public boolean isGroup() {
			if (Strings.isBlank(StrTalker)) {
				return false;
			}
			return StrTalker.contains("@chatroom");
		}
	}
}
