package com.zhuweiyou.msgbot.plugin.countdown;

import com.zhuweiyou.msgbot.platform.Msg;
import com.zhuweiyou.msgbot.platform.Platform;
import com.zhuweiyou.msgbot.plugin.CommandPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CountdownPlugin extends CommandPlugin {
	private final Countdown countdown;

	@Autowired
	public CountdownPlugin(Countdown countdown) {
		super("djs", "倒计时");
		this.countdown = countdown;
	}

	@Override
	public void execute(Msg msg, Platform platform) {
		try {
			platform.replyText(msg, countdown.getHolidays());
		} catch (Exception e) {
			platform.replyText(msg, e.getMessage());
		}
	}
}
