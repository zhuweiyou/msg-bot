package com.zhuweiyou.msgbot.platform.wxbot;

import com.zhuweiyou.msgbot.common.ImageUtil;
import com.zhuweiyou.msgbot.common.StringUtil;
import com.zhuweiyou.msgbot.platform.Group;
import com.zhuweiyou.msgbot.platform.Msg;
import com.zhuweiyou.msgbot.platform.Platform;
import com.zhuweiyou.msgbot.platform.User;
import com.zhuweiyou.msgbot.platform.wxbot.client.*;
import com.zhuweiyou.msgbot.store.MemoryMsgStore;
import com.zhuweiyou.msgbot.store.MsgStore;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

@Slf4j
@Component
public class WxbotPlatform implements Platform {
	private final WxbotConfig wxbotConfig;
	private final MsgStore msgStore = new MemoryMsgStore();
	private final WxbotClient wxbotClient;

	@Autowired
	public WxbotPlatform(WxbotConfig wxbotConfig) {
		this.wxbotConfig = wxbotConfig;
		this.wxbotClient = new WxbotClient(wxbotConfig.getApiUrl());
	}

	public Optional<Msg> parseBody(WxbotWebhookBody body) {
		WxbotWebhookBody.Data data = body.getData().getFirst();
		if (data == null) {
			return Optional.empty();
		}

		Msg msg = new Msg();
		if (data.isGroup()) {
			msg.setGroupId(Objects.toString(data.getStrTalker(), ""));
			if (data.isSelf()) {
				msg.setUserId(wxbotConfig.getBotWxid());
			} else {
				msg.setUserId(Objects.toString(data.getSender(), ""));
			}
		} else {
			msg.setUserId(Objects.toString(data.getStrTalker(), ""));
			msg.setGroupId("");
		}

		msg.setText(Objects.toString(data.getText(), ""));
		msg.setRaw(Objects.toString(data.getRaw(), ""));
		msg.setId(Objects.toString(data.getMsgSvrID(), ""));
		msg.setAtUserIds(List.of());
		msg.setAtBot(false);
		msg.setAdmin(Objects.equals(msg.getUserId(), wxbotConfig.getAdminWxid()));

		// 机器人发出的消息
		if (Objects.equals(wxbotConfig.getBotWxid(), msg.getUserId())) {
			return Optional.empty();
		}

		// 公众号之类的消息
		if (msg.getUserId().startsWith("gh_")) {
			return Optional.empty();
		}

		// 机器人引用他人的消息
		String fromusername = StringUtil.getMiddle(msg.getRaw(), "<fromusername>", "</fromusername>");
		if (Objects.equals(wxbotConfig.getBotWxid(), fromusername)) {
			return Optional.empty();
		}

		// text 和 raw 都是空的
		if (Strings.isBlank(msg.getText()) && Strings.isBlank(msg.getRaw())) {
			return Optional.empty();
		}

		msgStore.save(msg);

		// 让引用消息(包括引用卡片) 也能响应
		if (Strings.isBlank(msg.getText()) && Strings.isNotBlank(msg.getRaw())) {
			Msg originMsg = new Msg();
			String replyId = StringUtil.getMiddle(msg.getRaw(), "<svrid>", "</svrid>").trim();
			String replyMsg = StringUtil.getMiddle(msg.getRaw(), "<title>", "</title>").trim();
			if (Strings.isNotBlank(replyId)) {
				Optional<Msg> optionalMsg = msgStore.find(replyId);
				if (optionalMsg.isPresent()) {
					originMsg = optionalMsg.get();
				}
			}
			String originText = originMsg.getText();
			if (Strings.isBlank(originText)) {
				originText = HtmlUtils
					.htmlUnescape(StringUtil.getMiddle(msg.getRaw(), "&lt;title&gt;", "&lt;/title&gt;"))
					.trim();
			}
			if (Strings.isBlank(originText)) {
				originText = HtmlUtils
					.htmlUnescape(StringUtil.getMiddle(msg.getRaw(), "&lt;url&gt;", "&lt;/url&gt;"))
					.trim();
			}
			if (Strings.isNotBlank(originText)) {
				originMsg.setText(String.format("%s %s", replyMsg, originText));
				originMsg.setRaw(msg.getRaw());
				msg = originMsg;
			}
		}

		return Optional.of(msg);
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
		request.setContent(text);
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
		if (ImageUtil.isRemote(image)) {
			Optional<String> optionalString = ImageUtil.toBase64(image);
			if (optionalString.isEmpty()) {
				return;
			}

			request.setImage(optionalString.get());
		} else {
			if (ImageUtil.hasExt(image)) {
				request.setPath(image);
			} else {
				request.setImage(ImageUtil.trimBase64Prefix(image));
			}
		}
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
			sendGroupText(msg.getGroupId(), text, List.of(msg.getUserId()));
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
		user.setName(Objects.toString(data.get("nickname"), ""));
		user.setAvatar(Objects.toString(data.get("profilePicture"), ""));
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

		Map<String, Object> data = response.getData();
		List<User> users = new ArrayList<>();
		for (String key : data.keySet()) {
			Map<String, Object> item = (Map<String, Object>) data.get(key);
			User user = new User();
			user.setId(key);
			user.setName(Objects.toString(item.get("nickname"), ""));
			user.setAvatar(Objects.toString(item.get("profilePicture"), ""));
			users.add(user);
		}
		group.setUsers(users);
		return Optional.of(group);
	}

	@Override
	@Cacheable(value = "WxbotPlatform.getUserName", key = "#userId + #groupId")
	public String getUserName(String userId, String groupId) {
		String displayName = null;
		if (Strings.isNotBlank(groupId)) {
			Optional<Group> optionalGroup = getGroup(groupId);
			if (optionalGroup.isPresent()) {
				Optional<User> optionalUser = optionalGroup.get().getUsers().stream()
					.filter(user -> Objects.equals(userId, user.getId())).findFirst();
				if (optionalUser.isPresent()) {
					displayName = optionalUser.get().getName();
				}
			}
		}
		if (Strings.isBlank(displayName)) {
			Optional<User> optionalUser = getUser(userId);
			if (optionalUser.isPresent()) {
				displayName = optionalUser.get().getName();
			}
		}
		if (Strings.isBlank(displayName)) {
			displayName = userId;
		}
		return displayName;
	}
}
