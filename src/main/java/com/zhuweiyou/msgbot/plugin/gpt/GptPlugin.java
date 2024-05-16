package com.zhuweiyou.msgbot.plugin.gpt;

import com.zhuweiyou.msgbot.platform.Msg;
import com.zhuweiyou.msgbot.platform.Platform;
import com.zhuweiyou.msgbot.plugin.CommandPlugin;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GptPlugin extends CommandPlugin {
	private final Gpt gpt;

	@Autowired
	public GptPlugin(MoonshotGpt moonshotGpt) {
		super(true, "c", "g", "gpt", "chatgpt");
		this.gpt = moonshotGpt;
	}

	@Override
	public void execute(Msg msg, Platform platform) {
		String content = msg.getContent();
		if (!match(msg)) {
			content = msg.getText();
		}
		if (Strings.isBlank(content)) {
			return;
		}

		String result;
		try {
			result = gpt.prompt(content);
		} catch (Exception ignored) {
			try {
				result = gpt.prompt(content);
			} catch (Exception e) {
				result = e.getMessage();
			}
		}
		platform.replyText(msg, result);
	}
}
