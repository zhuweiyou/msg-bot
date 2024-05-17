package com.zhuweiyou.msgbot.common;

import org.apache.commons.codec.binary.Base64;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.FileCopyUtils;

import java.net.URI;
import java.util.Optional;
import java.util.regex.Pattern;

public class ImageUtil {
	public static Optional<String> toBase64(String imagePath) {
		try {
			Resource resource = new UrlResource(new URI(imagePath));
			byte[] imageBytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
			return Optional.of(Base64.encodeBase64String(imageBytes));
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	public static String trimBase64Prefix(String base64Image) {
		if (base64Image.startsWith("data:image/")) {
			return base64Image.substring(base64Image.indexOf(",") + 1);
		}
		return base64Image;
	}

	private final static Pattern IMAGE_EXT_PATTERN = Pattern.compile("\\.(gif|png|je?pg)", Pattern.CASE_INSENSITIVE);

	public static boolean hasExt(String imagePath) {
		return IMAGE_EXT_PATTERN.matcher(imagePath).find();
	}

	public static boolean isRemote(String imagePath) {
		// 简单判断一下 不够严谨 但是够用
		return imagePath.toLowerCase().startsWith("http");
	}
}
