package com.zhuweiyou.msgbot.platform;

import java.util.List;
import java.util.Optional;

public interface Platform {
	String getBotUserId();

	String getAdminUserId();

	void sendGroupText(String groupId, String text);

	void sendGroupText(String groupId, String text, List<String> atUserIds);

	void sendGroupImage(String groupId, String image);

	void sendPrivateText(String userId, String text);

	void sendPrivateImage(String userId, String image);

	void replyText(Msg msg, String text);

	void replyImage(Msg msg, String image);

	Optional<User> getUser(String userId);

	Optional<Group> getGroup(String groupId);

	String getUserName(String userId, String groupId);
}
