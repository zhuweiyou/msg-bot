package com.zhuweiyou.msgbot.platform.ntchat;

import com.zhuweiyou.msgbot.common.ImageUtil;
import com.zhuweiyou.msgbot.common.StringUtil;
import com.zhuweiyou.msgbot.platform.Group;
import com.zhuweiyou.msgbot.platform.Msg;
import com.zhuweiyou.msgbot.platform.Platform;
import com.zhuweiyou.msgbot.platform.User;
import com.zhuweiyou.msgbot.platform.ntchat.client.*;
import com.zhuweiyou.msgbot.store.MemoryMsgStore;
import com.zhuweiyou.msgbot.store.MsgStore;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
public class NtchatPlatform implements Platform {
	private final NtchatConfig ntchatConfig;
	private final MsgStore msgStore = new MemoryMsgStore();
	private final NtchatClient ntchatClient;

	@Autowired
	public NtchatPlatform(NtchatConfig ntchatConfig) {
		this.ntchatConfig = ntchatConfig;
		this.ntchatClient = new NtchatClient(ntchatConfig.getApiUrl(), ntchatConfig.getGuid());
	}

	public Optional<Msg> parseBody(NtchatWebhookBody body) {
		Msg msg = new Msg();

		NtchatWebhookBody.Data data = body.getMessage().getData();
		msg.setUserId(Objects.toString(data.getFrom_wxid(), ""));
		msg.setText(Objects.toString(data.getMsg(), ""));
		msg.setRaw(Objects.toString(data.getRaw_msg(), ""));
		msg.setId(Objects.toString(data.getMsgid(), ""));
		msg.setGroupId(Objects.toString(data.getRoom_wxid(), ""));
		msg.setAtUserIds(Optional.ofNullable(data.getAt_user_list()).orElse(List.of()));
		msg.setAtBot(msg.getAtUserIds().stream().anyMatch(userId -> Objects.equals(ntchatConfig.getBotWxid(), userId)));
		msg.setAdmin(Objects.equals(data.getFrom_wxid(), ntchatConfig.getAdminWxid()));

		// 机器人发出的消息
		if (Objects.equals(ntchatConfig.getBotWxid(), msg.getUserId())) {
			return Optional.empty();
		}

		// 公众号之类的消息
		if (msg.getUserId().startsWith("gh_")) {
			return Optional.empty();
		}

		// 机器人引用他人的消息
		String fromusername = StringUtil.getMiddle(msg.getRaw(), "<fromusername>", "</fromusername>");
		if (Objects.equals(ntchatConfig.getBotWxid(), fromusername)) {
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

	public String setCallbackUrl(String callback_url, boolean createClient) {
		NtchatResponse response;
		String guid = null;
		if (createClient) {
			response = ntchatClient.sendRequest(new NtchatCreateClientRequest());
			if (!response.isSuccess()) {
				return response.getMsg();
			}

			guid = response.getData().get("guid").toString();
			ntchatConfig.setGuid(guid);
			ntchatClient.setGuid(guid);

			response = ntchatClient.sendRequest(new NtchatOpenClientRequest(true));
			if (!response.isSuccess()) {
				return response.getMsg();
			}
		}

		response = ntchatClient.sendRequest(new NtchatSetCallbackUrlRequest(callback_url));
		if (!response.isSuccess()) {
			return response.getMsg();
		}

		return guid;
	}

	@Override
	public String getBotUserId() {
		return ntchatConfig.getBotWxid();
	}

	@Override
	public void sendGroupText(String groupId, String text) {
		if (Strings.isBlank(text)) {
			return;
		}

		ntchatClient.sendRequest(new NtchatSendTextRequest(groupId, text));
	}

	@Override
	public void sendGroupText(String groupId, String text, List<String> atUserIds) {
		if (Strings.isBlank(text)) {
			return;
		}

		ntchatClient.sendRequest(new NtchatSendRoomAtRequest(groupId, text, atUserIds));
	}

	@Override
	public void sendPrivateText(String userId, String text) {
		if (Strings.isBlank(text)) {
			return;
		}

		ntchatClient.sendRequest(new NtchatSendTextRequest(userId, text));
	}

	@Override
	public void sendGroupImage(String groupId, String image) {
		// ntchat 群里发图 和 私聊发图 是一样的
		sendPrivateImage(groupId, image);
	}

	@Override
	public void sendPrivateImage(String userId, String image) {
		if (Strings.isBlank(image)) {
			return;
		}

		if (!ImageUtil.isRemote(image) && !ImageUtil.hasExt(image)) {
			log.warn("ntchat不支持这种图片格式 {}", image);
			return;
		}

		NtchatRequestWithImage request;
		if (image.toLowerCase().contains(".gif")) {
			request = new NtchatSendGifRequest();
		} else {
			request = new NtchatSendImageRequest();
		}
		request.setTo_wxid(userId);
		request.setImage(image);
		ntchatClient.sendRequest(request);
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
	public void replyText(Msg msg, String text) {
		if (Strings.isBlank(text)) {
			return;
		}

		NtchatReplyTextRequest request = new NtchatReplyTextRequest();
		if (Strings.isBlank(msg.getGroupId())) {
			request.setTo_wxid(msg.getUserId());
		} else {
			request.setTo_wxid(msg.getGroupId());
		}
		request.setText(text.trim());
		request.setBot_wxid(ntchatConfig.getBotWxid());
		request.setFrom_wxid(msg.getUserId());
		request.setFrom_name(getUserName(msg.getUserId(), msg.getGroupId()));
		request.setFrom_id(msg.getId());
		request.setFrom_msg(msg.getText());
		ntchatClient.sendRequest(request);
	}

	@Override
	@Cacheable(value = "NtchatPlatform.getUser", key = "#userId")
	public Optional<User> getUser(String userId) {
		NtchatResponse response = ntchatClient.sendRequest(new NtchatGetContactDetailRequest(userId));
		if (!response.isSuccess()) {
			return Optional.empty();
		}

		Map<String, Object> data = response.getData();
		User user = new User();
		user.setId(data.get("wxid").toString());
		user.setName(data.get("nickname").toString());
		user.setAvatar(data.get("avatar").toString());
		return Optional.of(user);
	}

	@SuppressWarnings("unchecked")
	@Override
	@Cacheable(value = "NtchatPlatform.getGroup", key = "#groupId")
	public Optional<Group> getGroup(String groupId) {
		NtchatResponse response = ntchatClient.sendRequest(new NtchatGetRoomMembersRequest(groupId));
		if (!response.isSuccess()) {
			return Optional.empty();
		}

		Map<String, Object> data = response.getData();
		List<Map<String, Object>> memberList = (List<Map<String, Object>>) data.get("member_list");
		Group group = new Group();
		group.setId(data.get("group_wxid").toString());
		// group.setName(data.get("room_name").toString());
		// group.setAvatar(data.get("room_avatar").toString());
		group.setUsers(memberList.stream().map(member -> {
			User user = new User();
			user.setId(member.get("wxid").toString());
			String name = member.get("display_name").toString();
			if (Strings.isBlank(name)) {
				name = member.get("nickname").toString();
			}
			user.setName(name);
			user.setAvatar(member.get("avatar").toString());
			return user;
		}).toList());
		return Optional.of(group);
	}

	@Override
	@Cacheable(value = "NtchatPlatform.getUserName", key = "#userId + #groupId")
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

	@Override
	public String getAdminUserId() {
		return ntchatConfig.getAdminWxid();
	}
}
