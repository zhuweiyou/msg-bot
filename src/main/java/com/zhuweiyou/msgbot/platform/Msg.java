package com.zhuweiyou.msgbot.platform;

import lombok.Data;
import org.apache.logging.log4j.util.Strings;

import java.io.Serializable;
import java.util.List;
import java.util.regex.Pattern;

@Data
public class Msg implements Serializable {
	private String text;
	private String raw;
	private String id;
	private String userId;
	private String groupId;
	private List<String> atUserIds;
	private boolean atBot;
	private boolean admin;

	private static final Pattern COMMAND_PATTERN = Pattern.compile("\\s+");

	public String getCommand() {
		if (Strings.isBlank(text)) {
			return "";
		}
		return COMMAND_PATTERN.split(text.trim(), 2)[0];
	}

	public String getContent() {
		if (Strings.isBlank(text)) {
			return "";
		}
		return text.replaceFirst(Pattern.quote(getCommand()), "").trim();
	}
}
