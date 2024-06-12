package com.zhuweiyou.msgbot.platform.ntchat;

import com.zhuweiyou.msgbot.common.AppConfig;
import com.zhuweiyou.msgbot.plugin.PluginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/ntchat")
public class NtchatController {
	private final AppConfig appConfig;
	private final NtchatPlatform ntchatPlatform;
	private final PluginService pluginService;

	@Autowired
	public NtchatController(AppConfig appConfig, NtchatPlatform ntchatPlatform, PluginService pluginService) {
		this.appConfig = appConfig;
		this.ntchatPlatform = ntchatPlatform;
		this.pluginService = pluginService;
	}

	@PostMapping("/webhook")
	public String webhook(@RequestBody NtchatWebhookBody body) {
		log.info("NtchatController.webhook {}", body);
		ntchatPlatform.parseBody(body).ifPresent(msg -> pluginService.matchAll(msg, ntchatPlatform));
		return "received";
	}

	@PostMapping("/set-webhook")
	public String setWebhook(@RequestBody NtchatSetWebhookBody body) {
		return ntchatPlatform.setCallbackUrl(appConfig.getServerUrl() + "/ntchat/webhook", body.isCreateClient());
	}
}
