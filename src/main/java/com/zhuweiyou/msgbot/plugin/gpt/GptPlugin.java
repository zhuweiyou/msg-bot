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
		// 私聊默认可以不带命令前缀
		String content = match(msg) ? msg.getContent() : msg.getText();
		if (Strings.isBlank(content)) {
			return;
		}

		gptSet.forEach(gpt -> CompletableFuture.runAsync(() -> {
			try {
				platform.replyText(msg, String.format("【%s】\n%s", gpt.getName(), gpt.prompt(content)));
			} catch (Exception e) {
				// 失败不回复
				log.error("GptPlugin execute {} error", gpt.getName(), e);
			}
		}));
	}
}
