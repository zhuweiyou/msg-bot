package com.zhuweiyou.msgbot.platform.wxbot;

import com.zhuweiyou.msgbot.plugin.Plugin;
import com.zhuweiyou.msgbot.plugin.gpt.GptPlugin;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/wxbot")
public class WxbotController {
	private final WxbotPlatform wxbotPlatform;
	private final Set<Plugin> pluginSet;
	private final GptPlugin gptPlugin;

	@Autowired
	public WxbotController(WxbotPlatform wxbotPlatform, Set<Plugin> pluginSet, GptPlugin gptPlugin) {
		this.wxbotPlatform = wxbotPlatform;
		this.pluginSet = pluginSet;
		this.gptPlugin = gptPlugin;
	}

	@PostMapping("/webhook")
	public String webhook(@RequestBody WxbotWebhookBody body) {
		log.info("WxbotController.webhook {}", body);

		wxbotPlatform.parseBody(body).ifPresent(msg -> {
			boolean matched = false;
			for (Plugin plugin : pluginSet) {
				if (plugin.match(msg)) {
					matched = true;
					CompletableFuture.runAsync(() -> plugin.execute(msg, wxbotPlatform));
				}
			}
			// 插件都没匹配上, 私聊默认用GPT回复
			if (!matched && Strings.isBlank(msg.getGroupId())) {
				CompletableFuture.runAsync(() -> gptPlugin.execute(msg, wxbotPlatform));
			}
		});

		return "received";
	}
}
