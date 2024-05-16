package com.zhuweiyou.msgbot.platform;

import lombok.Data;

import java.util.List;

@Data
public class Group {
	private String id;
	private String name;
	private String avatar;
	private List<User> users;
}
