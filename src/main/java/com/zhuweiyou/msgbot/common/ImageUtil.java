package com.zhuweiyou.msgbot.common;

import org.apache.commons.codec.binary.Base64;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.FileCopyUtils;

import java.net.URI;
import java.util.Optional;

public class ImageUtil {
	public static Optional<String> encodeImageToBase64(String imagePath) {
		try {
			Resource resource = new UrlResource(new URI(imagePath));
			byte[] imageBytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
			return Optional.of(Base64.encodeBase64String(imageBytes));
		} catch (Exception e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}
}
