package com.zhuweiyou.msgbot.platform.ntchat;

import com.zhuweiyou.msgbot.common.AppConfig;
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
@RequestMapping("/ntchat")
public class NtchatController {
	private final AppConfig appConfig;
	private final NtchatPlatform ntchatPlatform;
	private final Set<Plugin> pluginSet;
	private final GptPlugin gptPlugin;

	@Autowired
	public NtchatController(AppConfig appConfig, NtchatPlatform ntchatPlatform, Set<Plugin> pluginSet,
	                        GptPlugin gptPlugin) {
		this.appConfig = appConfig;
		this.ntchatPlatform = ntchatPlatform;
		this.pluginSet = pluginSet;
		this.gptPlugin = gptPlugin;
	}

	@PostMapping("/webhook")
	public String webhook(@RequestBody NtchatWebhookBody body) {
		log.info("NtchatController.webhook {}", body);

		ntchatPlatform.parseBody(body).ifPresent(msg -> {
			boolean matched = false;
			for (Plugin plugin : pluginSet) {
				if (plugin.match(msg)) {
					matched = true;
					CompletableFuture.runAsync(() -> plugin.execute(msg, ntchatPlatform));
				}
			}
			// 插件都没匹配上, 私聊默认用GPT回复
			if (!matched && Strings.isBlank(msg.getGroupId())) {
				CompletableFuture.runAsync(() -> gptPlugin.execute(msg, ntchatPlatform));
			}
		});

		return "received";
	}

	@PostMapping("/set-webhook")
	public String setWebhook(@RequestBody NtchatSetWebhookBody body) {
		return ntchatPlatform.setCallbackUrl(appConfig.getServerUrl() + "/ntchat/webhook", body.isCreateClient());
	}
}
