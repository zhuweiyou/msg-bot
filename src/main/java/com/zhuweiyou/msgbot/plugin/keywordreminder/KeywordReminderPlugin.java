package com.zhuweiyou.msgbot.plugin.keywordreminder;

import com.zhuweiyou.msgbot.platform.Msg;
import com.zhuweiyou.msgbot.platform.Platform;
import com.zhuweiyou.msgbot.plugin.Plugin;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class KeywordReminderPlugin implements Plugin {
	private final KeywordReminderConfig keywordReminderConfig;

	@Autowired
	public KeywordReminderPlugin(KeywordReminderConfig keywordReminderConfig) {
		this.keywordReminderConfig = keywordReminderConfig;
	}

	@Override
	public boolean match(Msg msg) {
		if (Strings.isBlank(msg.getText()) || Strings.isBlank(msg.getGroupId())) {
			return false;
		}

		String text = msg.getText().replaceAll("\\s+", "").toLowerCase();
		for (String keyword : keywordReminderConfig.getKeywordList()) {
			// 包含任意一个, 触发
			if (text.contains(keyword.toLowerCase())) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void execute(Msg msg, Platform platform) {
		if (Objects.equals(msg.getUserId(), platform.getAdminUserId())) {
			return;
		}

		platform.sendPrivateText(platform.getAdminUserId(),
			String.format("【%s (%s) 在 %s 提到了你设定的关键词】\n%s",
				platform.getUserName(msg.getUserId(), msg.getGroupId()),
				msg.getUserId(),
				msg.getGroupId(),
				msg.getText()));
	}

}
