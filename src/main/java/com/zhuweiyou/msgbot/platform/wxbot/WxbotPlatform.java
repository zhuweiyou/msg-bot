package com.zhuweiyou.msgbot.platform.wxbot;

import com.zhuweiyou.msgbot.platform.Group;
import com.zhuweiyou.msgbot.platform.Msg;
import com.zhuweiyou.msgbot.platform.Platform;
import com.zhuweiyou.msgbot.platform.User;
import com.zhuweiyou.msgbot.platform.wxbot.client.*;
import com.zhuweiyou.msgbot.sensitiveword.SensitiveWord;
import com.zhuweiyou.msgbot.store.MemoryStore;
import com.zhuweiyou.msgbot.store.Store;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class WxbotPlatform implements Platform {
	private final WxbotConfig wxbotConfig;
	private final Store store = new MemoryStore();
	private final SensitiveWord sensitiveWord;
	private final WxbotClient wxbotClient;
	private final static Pattern IMAGE_PATH_PATTERN = Pattern.compile("\\.(gif|png|je?pg)", Pattern.CASE_INSENSITIVE);

	@Autowired
	public WxbotPlatform(WxbotConfig wxbotConfig, SensitiveWord sensitiveWord) {
		this.wxbotConfig = wxbotConfig;
		this.sensitiveWord = sensitiveWord;
		this.wxbotClient = new WxbotClient(wxbotConfig.getApiUrl());
	}

	@Override
	public String getBotUserId() {
		return wxbotConfig.getBotWxid();
	}

	@Override
	public String getAdminUserId() {
		return wxbotConfig.getAdminWxid();
	}

	@Override
	public void sendGroupText(String groupId, String text) {
		sendGroupText(groupId, text, List.of());
	}

	@Override
	public void sendGroupText(String groupId, String text, List<String> atUserIds) {
		if (Strings.isBlank(text)) {
			return;
		}
		WxbotSendTxtMsgRequest request = new WxbotSendTxtMsgRequest();
		request.setWxid(groupId);
		request.setContent(sensitiveWord.replace(text));
		request.setAtlist(atUserIds);
		wxbotClient.sendRequest(request);
	}


	@Override
	public void sendGroupImage(String groupId, String image) {
		if (Strings.isBlank(image)) {
			return;
		}
		WxbotSendImgMsgRequest request = new WxbotSendImgMsgRequest();
		request.setWxid(groupId);
		if (IMAGE_PATH_PATTERN.matcher(image).find()) {
			request.setPath(image);
		} else {
			request.setImage(image);
		}
		request.setImage(image);
		wxbotClient.sendRequest(request);
	}

	@Override
	public void sendPrivateText(String userId, String text) {
		if (Strings.isBlank(text)) {
			return;
		}
		WxbotSendTxtMsgRequest request = new WxbotSendTxtMsgRequest();
		request.setWxid(userId);
		request.setContent(text);
		wxbotClient.sendRequest(request);
	}

	@Override
	public void sendPrivateImage(String userId, String image) {
		// 和群里发一样
		sendGroupImage(userId, image);
	}

	@Override
	public void replyText(Msg msg, String text) {
		if (Strings.isBlank(msg.getGroupId())) {
			sendPrivateText(msg.getUserId(), text);
		} else {
			sendGroupText(msg.getGroupId(), text);
		}
	}

	@Override
	public void replyImage(Msg msg, String image) {
		if (Strings.isBlank(msg.getGroupId())) {
			sendPrivateImage(msg.getUserId(), image);
		} else {
			sendGroupImage(msg.getGroupId(), image);
		}
	}

	@Override
	@Cacheable(value = "WxbotPlatform.getUser", key = "#userId")
	public Optional<User> getUser(String userId) {
		WxbotAccountByWxidRequest request = new WxbotAccountByWxidRequest();
		request.setWxid(userId);
		WxbotResponse response = wxbotClient.sendRequest(request);
		if (!response.isSuccess()) {
			return Optional.empty();
		}
		Map<String, Object> data = response.getData();
		User user = new User();
		user.setId(userId);
		user.setName(data.get("nickname").toString());
		user.setAvatar(data.get("profilePicture").toString());
		return Optional.of(user);
	}

	@Override
	@Cacheable(value = "WxbotPlatform.getGroup", key = "#groupId")
	public Optional<Group> getGroup(String groupId) {
		Optional<User> optionalGroup = getUser(groupId);
		if (optionalGroup.isEmpty()) {
			return Optional.empty();
		}

		WxbotChatRoomRequest request = new WxbotChatRoomRequest();
		request.setWxid(groupId);
		WxbotResponse response = wxbotClient.sendRequest(request);
		if (!response.isSuccess()) {
			return Optional.empty();
		}

		Group group = new Group();
		group.setId(groupId);
		group.setName(optionalGroup.get().getName());
		group.setAvatar(optionalGroup.get().getAvatar());

//		response.getData()
//		group.setUsers();

		return Optional.empty();
	}

	@Override
	public String getUserName(String userId, String groupId) {
		Optional<User> optionalUser = getUser(userId);
		if (optionalUser.isEmpty()) {
			return userId;
		}
		return optionalUser.get().getName();
	}
}
