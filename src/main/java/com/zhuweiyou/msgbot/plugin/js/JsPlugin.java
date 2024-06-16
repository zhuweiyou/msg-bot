package com.zhuweiyou.msgbot.plugin.js;

import com.zhuweiyou.msgbot.common.StringUtil;
import com.zhuweiyou.msgbot.platform.Msg;
import com.zhuweiyou.msgbot.platform.Platform;
import com.zhuweiyou.msgbot.plugin.CommandPlugin;
import org.graalvm.polyglot.Context;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class JsPlugin extends CommandPlugin {
	private final static Map<String, String> MATH_CHARS_MAP = Map.of(
		"　", " ",
		"（", "(",
		"）", ")",
		"×", "*",
		"x", "*",
		"X", "*",
		"÷", "/"
	);

	public JsPlugin() {
		super(true, "js", "javascript");
	}

	@Override
	public void execute(Msg msg, Platform platform) {
		String content = msg.getContent();
		String result;
		try {
			// 正常运行
			result = evalJs(content);
		} catch (Exception e) {
			result = e.getMessage();
			try {
				// 如果失败了 替换数学符号再运行
				result = evalJs(replaceMathChars(content));
			} catch (Exception ignored) {
				// 仍然失败 使用正常运行的错误去返回
			}
		}

		result = StringUtil.limitCount(result, 520);
		result = StringUtil.limitRow(result, 10);
		platform.replyText(msg, result);
	}

	private String evalJs(String code) {
		try (Context context = Context.create()) {
			return context.eval("js", code).toString();
		}
	}

	private String replaceMathChars(String input) {
		for (Map.Entry<String, String> entry : MATH_CHARS_MAP.entrySet()) {
			input = input.replace(entry.getKey(), entry.getValue());
		}

		return input;
	}
}
