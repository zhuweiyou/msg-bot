package com.zhuweiyou.msgbot.platform.wxbot;

import com.zhuweiyou.msgbot.plugin.PluginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/wxbot")
public class WxbotController {
	private final WxbotPlatform wxbotPlatform;
	private final PluginService pluginService;

	@Autowired
	public WxbotController(WxbotPlatform wxbotPlatform, PluginService pluginService) {
		this.wxbotPlatform = wxbotPlatform;
		this.pluginService = pluginService;
	}

	@PostMapping("/webhook")
	public String webhook(@RequestBody WxbotWebhookBody body) {
		log.info("WxbotController.webhook {}", body);
		wxbotPlatform.parseBody(body).ifPresent(msg -> pluginService.matchAll(msg, wxbotPlatform));
		return "received";
	}

	@PostMapping("/qrcode")
	public String qrcode(@RequestBody Map<String, Object> body) {
		log.info("WxbotController.qrcode {}", body);
		return "received";
	}
}
