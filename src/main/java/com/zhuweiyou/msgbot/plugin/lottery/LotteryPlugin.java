package com.zhuweiyou.msgbot.plugin.lottery;

import com.zhuweiyou.msgbot.platform.Group;
import com.zhuweiyou.msgbot.platform.Msg;
import com.zhuweiyou.msgbot.platform.Platform;
import com.zhuweiyou.msgbot.platform.User;
import com.zhuweiyou.msgbot.plugin.CommandPlugin;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class LotteryPlugin extends CommandPlugin {
	public LotteryPlugin() {
		super("cj", "choujiang", "抽奖", "lottery");
	}

	@Override
	public void execute(Msg msg, Platform platform) {
		if (Strings.isBlank(msg.getGroupId())) {
			platform.replyText(msg, "抽奖功能仅群聊可用");
			return;
		}

		String content = msg.getContent();
		if (content.length() > 20) {
			platform.replyText(msg, "奖品名称这么长? 你抽个毛啊!");
			return;
		}

		// 排除机器人和发起者
		List<String> excludeList = List.of(platform.getBotUserId(), msg.getUserId());
		Optional<Group> optionalGroup = platform.getGroup(msg.getGroupId());
		List<String> userList = new ArrayList<String>();
		if (optionalGroup.isPresent()) {
			userList = new ArrayList<>(optionalGroup.get().getUsers().stream().map(User::getId)
				.filter(wxid -> !excludeList.contains(wxid)).toList());
		}
		if (userList.isEmpty()) {
			platform.replyText(msg, "没有获取到群成员列表, 抽奖失败");
			return;
		}

		Collections.shuffle(userList);
		String userId = userList.getFirst();
		String userName = platform.getUserName(userId, msg.getGroupId());

		String result;
		if (Strings.isBlank(content)) {
			result = "恭喜" + userName + "(" + userId + ")" + "中奖";
		} else {
			result = "恭喜" + userName + "(" + userId + ")" + "中奖【" + content + "】";
		}
		platform.sendGroupText(msg.getGroupId(), result, List.of(userId));
	}
}
