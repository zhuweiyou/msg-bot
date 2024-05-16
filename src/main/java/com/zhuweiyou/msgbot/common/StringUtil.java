package com.zhuweiyou.msgbot.common;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class StringUtil {
	public static String getMiddle(String src, String left, String right) {
		return getMiddle(src, left, right, false);
	}

	public static String getMiddle(String src, String left, String right, boolean more) {
		if (src == null || left == null || right == null) {
			return "";
		}

		int leftIndex = src.indexOf(left);
		if (leftIndex == -1) {
			return "";
		}

		leftIndex += left.length();
		String sub = src.substring(leftIndex);
		int rightIndex = more ? sub.lastIndexOf(right) : sub.indexOf(right);
		if (rightIndex == -1) {
			return "";
		}

		return src.substring(leftIndex, leftIndex + rightIndex);
	}

	public static String limitCount(String input, int limit) {
		if (limit <= 0) {
			return input;
		}

		if (input.length() > limit) {
			input = String.format("%s\n\n防止刷屏仅显示前%d字",
				input.substring(0, limit),
				limit);
		}

		return input;
	}

	public static String limitRow(String input, int limit) {
		if (limit <= 0) {
			return input;
		}

		String[] rows = input.split("\n");
		if (rows.length > limit) {
			input = String.format("%s\n\n防止刷屏仅显示前%d行",
				Arrays.stream(rows).limit(limit).collect(Collectors.joining("\n")),
				limit);
		}

		return input;
	}

	private final static Map<String, String> MATH_CHARS_MAP = Map.of(
		"　", " ",
		"（", "(",
		"）", ")",
		"×", "*",
		"x", "*",
		"X", "*",
		"÷", "/"
	);

	public static String replaceMathChars(String input) {
		for (Map.Entry<String, String> entry : MATH_CHARS_MAP.entrySet()) {
			input = input.replace(entry.getKey(), entry.getValue());
		}

		return input;
	}
}
