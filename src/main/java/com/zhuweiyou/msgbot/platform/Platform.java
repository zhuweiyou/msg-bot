package com.zhuweiyou.msgbot.platform;

import java.util.List;
import java.util.Optional;

public interface Platform {
	String getBotUserId();

	String getAdminUserId();

	void sendGroupText(String groupId, String text);

	void sendGroupText(String groupId, String text, List<String> atUserIds);

	void sendGroupImage(String groupId, String urlOrFilePath);

	void sendGroupGif(String groupId, String urlOrFilePath);

	void sendPrivateText(String userId, String text);

	void sendPrivateImage(String userId, String urlOrFilePath);

	void sendPrivateGif(String userId, String urlOrFilePath);

	void replyText(Msg msg, String text);

	void replyImage(Msg msg, String urlOrFilePath);

	void replyGif(Msg msg, String urlOrFilePath);

	Optional<User> getUser(String userId);

	Optional<Group> getGroup(String groupId);

	String getUserName(String userId, String groupId);
}
