package com.zhuweiyou.msgbot.platform.wxbot.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpMethod;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WxbotSendImgMsgRequest implements WxbotRequest {
	private String wxid;
	// 图片路径
	private String path;
	// base64 （不需要加 data:image/jpeg;base64, 前缀）
	private String image;
	// 发完是否删除
	private boolean clear = true;

	@Override
	public HttpMethod method() {
		return HttpMethod.POST;
	}

	@Override
	public String path() {
		return "/api/send-img-msg";
	}
}
