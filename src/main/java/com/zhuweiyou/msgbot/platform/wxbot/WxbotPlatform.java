package com.zhuweiyou.msgbot.platform.wxbot;

import com.zhuweiyou.msgbot.platform.Group;
import com.zhuweiyou.msgbot.platform.Msg;
import com.zhuweiyou.msgbot.platform.Platform;
import com.zhuweiyou.msgbot.platform.User;

import java.util.List;
import java.util.Optional;

public class WxbotPlatform implements Platform {
	@Override
	public String getBotUserId() {
		return "";
	}

	@Override
	public String getAdminUserId() {
		return "";
	}

	@Override
	public void sendGroupText(String groupId, String text) {

	}

	@Override
	public void sendGroupText(String groupId, String text, List<String> atUserIds) {

	}

	@Override
	public void sendGroupImage(String groupId, String image) {

	}

	@Override
	public void sendPrivateText(String userId, String text) {

	}

	@Override
	public void sendPrivateImage(String userId, String image) {

	}

	@Override
	public void replyText(Msg msg, String text) {

	}

	@Override
	public void replyImage(Msg msg, String image) {

	}

	@Override
	public Optional<User> getUser(String userId) {
		return Optional.empty();
	}

	@Override
	public Optional<Group> getGroup(String groupId) {
		return Optional.empty();
	}

	@Override
	public String getUserName(String userId, String groupId) {
		return "";
	}
}
