package com.zhuweiyou.msgbot.cron.weibo;

import com.zhuweiyou.msgbot.platform.ntchat.NtchatPlatform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WeiboCron {
	private final WeiboConfig weiboConfig;
	private final WeiboWatcher ganggangWatcher;
	private final NtchatPlatform ntchatPlatform;

	@Autowired
	public WeiboCron(WeiboConfig weiboConfig, GanggangWeiboFilter ganggangWeiboFilter, NtchatPlatform ntchatPlatform) {
		this.weiboConfig = weiboConfig;
		this.ntchatPlatform = ntchatPlatform;
		this.ganggangWatcher = new WeiboWatcher("5514146941", ganggangWeiboFilter);
	}

	@Scheduled(cron = "0 */1 * * * *")
	public void watchGanggang() throws Exception {
		ganggangWatcher.getNewBlog().ifPresent(blog -> {
			String msg = blog.toString();
			for (String wxid : weiboConfig.getGanggangWxidList()) {
				ntchatPlatform.sendPrivateText(wxid, msg);
			}
		});
	}
}
