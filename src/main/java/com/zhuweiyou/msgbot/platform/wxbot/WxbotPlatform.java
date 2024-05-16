package com.zhuweiyou.msgbot.platform.wxbot;

import com.zhuweiyou.msgbot.common.StringUtil;
import com.zhuweiyou.msgbot.platform.Group;
import com.zhuweiyou.msgbot.platform.Msg;
import com.zhuweiyou.msgbot.platform.Platform;
import com.zhuweiyou.msgbot.platform.User;
import com.zhuweiyou.msgbot.platform.wxbot.client.*;
import com.zhuweiyou.msgbot.sensitiveword.SensitiveWord;
import com.zhuweiyou.msgbot.store.MemoryStore;
import com.zhuweiyou.msgbot.store.Store;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class WxbotPlatform implements Platform {
	private static final Logger log = LoggerFactory.getLogger(WxbotPlatform.class);
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

	public Optional<Msg> parseBody(WxbotWebhookBody body) {
		Msg msg = new Msg();

		WxbotWebhookBody.Data data = body.getData().getFirst();
		if (data == null) {
			return Optional.empty();
		}

		if (data.isGroup()) {
			if (data.isSelf()) {
				msg.setUserId(wxbotConfig.getBotWxid());
			} else {
				msg.setUserId(Objects.toString(data.getSender(), ""));
			}
		} else {
			msg.setUserId(Objects.toString(data.getStrTalker(), ""));
		}

		if (data.isXmlContent()) {
			msg.setText("");
			msg.setRaw(Objects.toString(data.getStrContent(), ""));
		} else {
			msg.setText(Objects.toString(data.getStrContent(), ""));
			msg.setRaw("");
		}

		msg.setId(Objects.toString(data.getMsgSvrID(), ""));
		if (data.isGroup()) {
			msg.setGroupId(Objects.toString(data.getStrTalker(), ""));
		} else {
			msg.setGroupId("");
		}

		msg.setAtUserIds(List.of());
		msg.setAtBot(false);
		msg.setAdmin(Objects.equals(msg.getUserId(), wxbotConfig.getAdminWxid()));

		// 调试阶段 只响应这个群
		if (!Objects.equals(msg.getGroupId(), "49610278360@chatroom")) {
			return Optional.empty();
		}

		// 机器人发出的消息
		if (Objects.equals(wxbotConfig.getBotWxid(), msg.getUserId())) {
			return Optional.empty();
		}

		// 机器人引用他人的消息
		String fromusername = StringUtil.getMiddle(msg.getRaw(), "<fromusername>", "</fromusername>");
		if (Objects.equals(wxbotConfig.getBotWxid(), fromusername)) {
			log.info("ms3 {}", msg);
			return Optional.empty();
		}

		// text 和 raw 都是空的
		if (Strings.isBlank(msg.getText()) && Strings.isBlank(msg.getRaw())) {
			return Optional.empty();
		}

		store.save(msg);

		// 让引用消息(包括引用卡片) 也能响应
		if (Strings.isBlank(msg.getText()) && Strings.isNotBlank(msg.getRaw())) {
			Msg originMsg = new Msg();
			String replyId = StringUtil.getMiddle(msg.getRaw(), "<svrid>", "</svrid>").trim();
			String replyMsg = StringUtil.getMiddle(msg.getRaw(), "<title>", "</title>").trim();
			if (Strings.isNotBlank(replyId)) {
				Optional<Msg> optionalMsg = store.find(replyId);
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