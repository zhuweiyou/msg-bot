package com.zhuweiyou.msgbot.plugin;

import com.zhuweiyou.msgbot.platform.Msg;
import com.zhuweiyou.msgbot.platform.Platform;
import com.zhuweiyou.msgbot.plugin.gpt.GptPlugin;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Component
public class PluginService {
	private final Set<Plugin> pluginSet;
	private final GptPlugin gptPlugin;

	@Autowired
	public PluginService(Set<Plugin> pluginSet, GptPlugin gptPlugin) {
		this.pluginSet = pluginSet;
		this.gptPlugin = gptPlugin;
	}

	// 机器人平台回复一般不限次数 可以全匹配
	public void matchAll(Msg msg, Platform platform) {
		boolean matched = false;
		for (Plugin plugin : pluginSet) {
			// 匹配上的全都触发
			if (plugin.match(msg)) {
				matched = true;
				CompletableFuture.runAsync(() -> plugin.execute(msg, platform));
			}
		}
		// 插件都没匹配上, 私聊默认用GPT回复
		if (!matched && Strings.isBlank(msg.getGroupId())) {
			CompletableFuture.runAsync(() -> gptPlugin.execute(msg, platform));
		}
	}

	// 某些平台只能被动一问一答
	public void matchOne(Msg msg, Platform platform) {
		for (Plugin plugin : pluginSet) {
			if (plugin.match(msg)) {
				CompletableFuture.runAsync(() -> plugin.execute(msg, platform));
				return;
			}
		}
		// 插件都没匹配上, 私聊默认用GPT回复
		if (Strings.isBlank(msg.getGroupId())) {
			CompletableFuture.runAsync(() -> gptPlugin.execute(msg, platform));
		}
	}
}
