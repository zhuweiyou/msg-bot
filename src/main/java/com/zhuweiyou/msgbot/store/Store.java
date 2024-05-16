package com.zhuweiyou.msgbot.store;

import com.zhuweiyou.msgbot.platform.Msg;

import java.util.Optional;

public interface Store {
	void save(Msg msg);

	Optional<Msg> find(String msgId);
}
