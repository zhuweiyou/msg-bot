package com.zhuweiyou.msgbot.platform.ntchat.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NtchatResponse {
	private int status;
	private String msg;
	private Map<String, Object> data = new HashMap<>();

	public boolean isSuccess() {
		return status == 1;
	}
}
