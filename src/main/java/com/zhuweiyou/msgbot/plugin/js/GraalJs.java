package com.zhuweiyou.msgbot.plugin.js;

import org.graalvm.polyglot.Context;
import org.springframework.stereotype.Component;

@Component
public class GraalJs implements Js {
	@Override
	public String eval(String code) {
		try (Context context = Context.create()) {
			return context.eval("js", code).toString();
		}
	}
}
