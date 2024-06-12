package com.zhuweiyou.msgbot.plugin.gpt;

public interface Gpt {
	String getName();

	String prompt(String input) throws Exception;
}
