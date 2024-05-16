package com.zhuweiyou.msgbot.plugin.raw;

import com.zhuweiyou.msgbot.platform.Msg;
import com.zhuweiyou.msgbot.platform.Platform;
import com.zhuweiyou.msgbot.plugin.CommandPlugin;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

@Component
public class RawPlugin extends CommandPlugin {
	public RawPlugin() {
		super("raw");
	}

	@Override
	public void execute(Msg msg, Platform platform) {
		platform.replyText(msg, Strings.isBlank(msg.getRaw()) ? msg.getText() : msg.getRaw());
	}
}
