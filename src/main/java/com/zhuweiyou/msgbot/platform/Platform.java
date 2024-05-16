package com.zhuweiyou.msgbot.platform;

import java.util.List;
import java.util.Optional;

public interface Platform {
	String getBotUserId();

	String getAdminUserId();

	void sendGroupText(String groupId, String text);

	void sendGroupText(String groupId, String text, List<String> atUserIds);

	void sendPrivateText(String userId, String text);

	void replyText(Msg msg, String text);

	Optional<User> getUser(String userId);

	Optional<Group> getGroup(String groupId);

	String getUserName(String userId, String groupId);
}
