package com.zhuweiyou.msgbot.cron.weibo;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.zhuweiyou.msgbot.platform.ntchat.NtchatPlatform;

@Component
public class WeiboCron {
	private final WeiboConfig weiboConfig;
	private final Set<WeiboWatcher> weiboWatcherSet;
	private final NtchatPlatform ntchatPlatform;

	@Autowired
	public WeiboCron(WeiboConfig weiboConfig, GanggangWeiboFilter ganggangWeiboFilter, NtchatPlatform ntchatPlatform) {
		this.weiboConfig = weiboConfig;
		this.ntchatPlatform = ntchatPlatform;
		this.weiboWatcherSet = Set.of(
			new WeiboWatcher("5514146941", ganggangWeiboFilter)
		);
	}

	@Scheduled(cron = "0 */1 * * * *")
	public void watchBlog() throws Exception {
		for (WeiboWatcher weiboWatcher : weiboWatcherSet) {
			weiboWatcher.getNewBlog().ifPresent(blog -> {
				String msg = blog.toString();
				for (String wxid : weiboConfig.getGanggangWxidList()) {
					ntchatPlatform.sendPrivateText(wxid, msg);
				}
			});
		}
	}
}
