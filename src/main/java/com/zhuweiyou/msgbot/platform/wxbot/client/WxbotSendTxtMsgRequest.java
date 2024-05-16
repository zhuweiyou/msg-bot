package com.zhuweiyou.msgbot.platform.wxbot.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WxbotSendTxtMsgRequest implements WxbotRequest {
	private String wxid;
	private String content;
	private List<String> atlist = new ArrayList<>();

	@Override
	public HttpMethod method() {
		return HttpMethod.POST;
	}

	@Override
	public String path() {
		return "/api/send-txt-msg";
	}

	public String getContent() {
		if (atlist.isEmpty()) {
			return content;
		}
		return atlist.stream().map(wxid -> "@" + wxid + " ").collect(Collectors.joining()) + "\n\n" + content;
	}
}
