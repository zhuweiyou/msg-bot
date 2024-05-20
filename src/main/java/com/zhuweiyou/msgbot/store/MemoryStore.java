package com.zhuweiyou.msgbot.store;

import com.zhuweiyou.msgbot.platform.Msg;
import org.apache.commons.lang3.SerializationUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class MemoryStore implements Store {
	private static final int SIZE_LIMIT = 1000;

	private final Map<String, Msg> msgMap = new LinkedHashMap<>() {
		@Override
		protected boolean removeEldestEntry(Map.Entry<String, Msg> eldest) {
			return this.size() > SIZE_LIMIT;
		}
	};

	public synchronized void save(Msg msg) {
		msgMap.put(msg.getId(), msg);
	}

	public Optional<Msg> find(String msgId) {
		Msg msg = msgMap.get(msgId);
		if (msg == null) {
			return Optional.empty();
		}
		return Optional.of(SerializationUtils.clone(msg));
	}
}
