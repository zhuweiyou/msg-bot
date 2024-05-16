package com.zhuweiyou.msgbot.plugin.testimg;

import com.zhuweiyou.msgbot.platform.Msg;
import com.zhuweiyou.msgbot.platform.Platform;
import com.zhuweiyou.msgbot.plugin.CommandPlugin;
import org.springframework.stereotype.Component;

@Component
public class TestImgPlugin extends CommandPlugin {
	public TestImgPlugin() {
		super("testimg");
	}

	@Override
	public void execute(Msg msg, Platform platform) {
//		platform.replyImage(msg, "https://wx.qlogo.cn/mmhead/ver_1/Mahicib1RWGuEDp1JBWmxEGoFDaicSGWtaEJbuic5FTUR6ibsPQSXIw4tP39VJ1CSNzGbNl09ib1WHuUtkJDpn6LYDm6ygaZXC5MficFJicO9Uia6nu4/0");
		platform.replyImage(msg, "C:\\work\\card-recorder\\jjddz\\hand\\2_2.png");
	}
}
