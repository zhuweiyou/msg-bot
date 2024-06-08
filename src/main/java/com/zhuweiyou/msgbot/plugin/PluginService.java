package com.zhuweiyou.msgbot.plugin;

import com.zhuweiyou.msgbot.platform.Msg;
import com.zhuweiyou.msgbot.platform.Platform;

public interface PluginService {
	void execute(Msg msg, Platform platform);
}
