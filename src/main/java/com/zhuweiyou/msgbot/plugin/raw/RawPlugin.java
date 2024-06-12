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
		// XML内容太长, 不开放给普通用户
		if (!msg.isAdmin()) {
			platform.replyText(msg, "RAW功能仅管理员调试用");
			return;
		}

		// 用于查看某些卡片的信息, 比如小程序地址参数等
		platform.replyText(msg, Strings.isBlank(msg.getRaw()) ? msg.getText() : msg.getRaw());
	}
}
