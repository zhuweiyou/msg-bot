package com.zhuweiyou.msgbot.platform.ntchat.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NtchatReplyTextRequest extends NtchatSendXmlRequest {
	private String to_wxid;
	@JsonIgnore
	private String text;
	@JsonIgnore
	private String bot_wxid;
	@JsonIgnore
	private String from_wxid;
	@JsonIgnore
	private String from_name;
	@JsonIgnore
	private String from_id;
	@JsonIgnore
	private String from_msg;

	@Override
	public String getXml() {
		Document document = DocumentHelper.createDocument();
		Element msg = document.addElement("msg");
		{
			msg.addElement("fromusername").setText(bot_wxid);
			Element appmsg = msg.addElement("appmsg");
			{
				appmsg.addElement("title").setText(text);
				appmsg.addElement("type").setText("57");
				Element refermsg = appmsg.addElement("refermsg");
				{
					refermsg.addElement("type").setText("1");
					refermsg.addElement("fromusr").setText(from_wxid);
					refermsg.addElement("displayname").setText(from_name);
					refermsg.addElement("content").setText(from_msg);
					refermsg.addElement("svrid").setText(from_id);
				}
			}
		}
		return document.asXML();
	}
}
