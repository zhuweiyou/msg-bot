package com.zhuweiyou.msgbot.cron.bilibili;

import com.zhuweiyou.msgbot.platform.ntchat.NtchatPlatform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BilibiliCron {
	private final BilibiliConfig bilibiliConfig;
	private final BilibiliWatcher chuanggeWatcher;
	private final NtchatPlatform ntchatPlatform;

	@Autowired
	public BilibiliCron(BilibiliConfig bilibiliConfig, NtchatPlatform ntchatPlatform) {
		this.bilibiliConfig = bilibiliConfig;
		this.ntchatPlatform = ntchatPlatform;
		this.chuanggeWatcher = new BilibiliWatcher("548196587");
	}

	@Scheduled(cron = "0 */5 * * * *")
	public void watchChuangge() throws Exception {
		chuanggeWatcher.getNewCard().ifPresent(card -> {
			String msg = card.toString();
			for (String wxid : bilibiliConfig.getChuanggeWxidList()) {
				ntchatPlatform.sendPrivateText(wxid, msg);
			}
		});
	}
}
