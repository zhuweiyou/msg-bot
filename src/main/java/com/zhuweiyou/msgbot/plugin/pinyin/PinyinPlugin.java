package com.zhuweiyou.msgbot.plugin.pinyin;

import com.github.houbb.pinyin.util.PinyinHelper;
import com.zhuweiyou.msgbot.platform.Msg;
import com.zhuweiyou.msgbot.platform.Platform;
import com.zhuweiyou.msgbot.plugin.CommandPlugin;
import org.springframework.stereotype.Component;

@Component
public class PinyinPlugin extends CommandPlugin {
	public PinyinPlugin() {
		super(true, "py", "pinyin", "拼音");
	}

	@Override
	public void execute(Msg msg, Platform platform) {
		platform.replyText(msg, PinyinHelper.toPinyin(msg.getContent()));
	}
}
