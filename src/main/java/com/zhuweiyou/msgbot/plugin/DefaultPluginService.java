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
public class DefaultPluginService implements PluginService {
	private final Set<Plugin> pluginSet;
	private final GptPlugin gptPlugin;

	@Autowired
	public DefaultPluginService(Set<Plugin> pluginSet, GptPlugin gptPlugin) {
		this.pluginSet = pluginSet;
		this.gptPlugin = gptPlugin;
	}

	@Override
	public void execute(Msg msg, Platform platform) {
		boolean matched = false;
		for (Plugin plugin : pluginSet) {
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
}
