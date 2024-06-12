package com.zhuweiyou.msgbot.plugin;

import com.zhuweiyou.msgbot.platform.Msg;
import org.apache.logging.log4j.util.Strings;

import java.util.Set;

public abstract class CommandPlugin implements Plugin {
	protected final Set<String> commandSet;
	// 去除指令开头之后 剩下的内容是否不为空
	// 比如 help 后面不需要跟内容 也可以触发
	// 比如 js 后面必须要有内容 才能触发
	protected final boolean contentRequired;

	public CommandPlugin(String... command) {
		this.commandSet = Set.of(command);
		this.contentRequired = false;
	}

	public CommandPlugin(boolean contentRequired, String... command) {
		this.commandSet = Set.of(command);
		this.contentRequired = contentRequired;
	}

	@Override
	public boolean match(Msg msg) {
		if (!commandSet.contains(msg.getCommand().toLowerCase())) {
			return false;
		}
		if (this.contentRequired) {
			return Strings.isNotBlank(msg.getContent());
		}
		return true;
	}
}
