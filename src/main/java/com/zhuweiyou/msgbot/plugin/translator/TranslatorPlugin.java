package com.zhuweiyou.msgbot.plugin.translator;

import com.zhuweiyou.msgbot.platform.Msg;
import com.zhuweiyou.msgbot.platform.Platform;
import com.zhuweiyou.msgbot.plugin.CommandPlugin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class TranslatorPlugin extends CommandPlugin {
	private final Set<Translator> translatorSet;

	@Autowired
	public TranslatorPlugin(Set<Translator> translatorSet) {
		super(true, "fy", "翻译");
		this.translatorSet = translatorSet;
	}

	@Override
	public void execute(Msg msg, Platform platform) {
		String content = msg.getContent();
		translatorSet.forEach(translator -> CompletableFuture.runAsync(() -> {
			try {
				platform.replyText(msg, String.format("【%s】\n%s", translator.getName(), translator.translate(content)));
			} catch (Exception e) {
				// 失败不回复
				log.error("TranslatorPlugin execute {} error", translator.getName(), e);
			}
		}));
	}
}
