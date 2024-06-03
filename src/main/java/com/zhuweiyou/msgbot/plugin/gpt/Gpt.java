package com.zhuweiyou.msgbot.plugin.gpt;

public interface Gpt {
	String name();
	String prompt(String input) throws Exception;
}
