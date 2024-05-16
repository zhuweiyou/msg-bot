package com.zhuweiyou.msgbot.plugin;

import com.zhuweiyou.msgbot.platform.Msg;
import com.zhuweiyou.msgbot.platform.Platform;

public interface Plugin {
	boolean match(Msg msg);

	void execute(Msg msg, Platform platform);
}
