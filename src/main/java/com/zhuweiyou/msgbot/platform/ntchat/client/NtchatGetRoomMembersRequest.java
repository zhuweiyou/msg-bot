package com.zhuweiyou.msgbot.platform.ntchat.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NtchatGetRoomMembersRequest extends NtchatRequestWithGuid {
	private String room_wxid;

	@Override
	public String path() {
		return "/room/get_room_members";
	}
}
