package com.zhuweiyou.msgbot.platform.wxbot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/wxbot")
public class WxbotController {
	@PostMapping("/webhook")
	public String webhook(@RequestBody Map<String, Object> body) {
		log.info("WxbotController.webhook {}", body);
		return "received";
	}
}
