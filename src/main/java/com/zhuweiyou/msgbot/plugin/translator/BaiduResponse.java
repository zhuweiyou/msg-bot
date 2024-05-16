package com.zhuweiyou.msgbot.plugin.translator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.apache.logging.log4j.util.Strings;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaiduResponse {
	private String error_msg;

	private List<TransResult> trans_result;

	public boolean isSuccess() {
		return Strings.isBlank(error_msg) && trans_result != null && !trans_result.isEmpty();
	}

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class TransResult {
		private String dst;
	}
}
