package com.zhuweiyou.msgbot.platform.ntchat.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public abstract class NtchatRequestWithImage extends NtchatRequestWithGuid {
	private String to_wxid;

	@JsonIgnore
	protected String image;

	public String getUrl() {
		if (image == null) {
			return null;
		}

		if (image.startsWith("http://") || image.startsWith("https://")) {
			return image;
		}

		return null;
	}

	public String getFile_path() {
		if (image == null) {
			return null;
		}

		if (image.startsWith("file://")) {
			return image.replaceFirst(Pattern.quote("file:///"), "");
		}

		if (getUrl() == null) {
			return image;
		}

		return null;
	}
}
