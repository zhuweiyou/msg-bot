package com.zhuweiyou.msgbot.plugin.help;

import com.zhuweiyou.msgbot.platform.Msg;
import com.zhuweiyou.msgbot.platform.Platform;
import com.zhuweiyou.msgbot.plugin.CommandPlugin;
import org.springframework.stereotype.Component;

@Component
public class HelpPlugin extends CommandPlugin {
	private final String helpText;

	public HelpPlugin() {
		super("h", "help", "帮助", "菜单");
		helpText = String.join("\n",
			"【帮助】",
			"[C或G] 问答机器人",
			"[CJ] 抽奖(随机1名群成员中奖)",
			"[DJS] 节日倒计时",
			"[FY] 中英互译",
			"[JS] 运行JS代码",
			"[RAW] 输出消息原始内容",
			"[PY] 汉字转拼音",
			"",
			"github.com/zhuweiyou/msg-bot");
	}

	@Override
	public void execute(Msg msg, Platform platform) {
		platform.replyText(msg, helpText);
	}
}
