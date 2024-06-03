package com.zhuweiyou.msgbot.plugin.gpt;

import com.zhuweiyou.msgbot.platform.Msg;
import com.zhuweiyou.msgbot.platform.Platform;
import com.zhuweiyou.msgbot.plugin.CommandPlugin;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class GptPlugin extends CommandPlugin {
	private final Set<Gpt> gptSet;

	@Autowired
	public GptPlugin(Set<Gpt> gptSet) {
		super(true, "c", "g", "gpt", "chatgpt");
		this.gptSet = gptSet;
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

		String finalContent = content;
		gptSet.forEach(gpt -> CompletableFuture.runAsync(() -> {
			try {
				platform.replyText(msg, String.join("\n", "【" + gpt.name() + "】", gpt.prompt(finalContent)));
			} catch (Exception e) {
				log.error("GptPlugin execute {} error", gpt.name(), e);
			}
		}));
	}
}
