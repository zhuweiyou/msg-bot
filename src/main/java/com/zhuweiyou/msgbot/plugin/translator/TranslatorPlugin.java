package com.zhuweiyou.msgbot.plugin.translator;

import com.zhuweiyou.msgbot.platform.Msg;
import com.zhuweiyou.msgbot.platform.Platform;
import com.zhuweiyou.msgbot.plugin.CommandPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class TranslatorPlugin extends CommandPlugin {
	private final Set<Translator> translatorSet;

	@Autowired
	public TranslatorPlugin(Set<Translator> translatorSet) {
		super(true, "fy", "翻译");
		this.translatorSet = translatorSet;
	}

	@Override
	public void execute(Msg msg, Platform platform) {
		List<CompletableFuture<TranslatorResult>> futures = translatorSet.stream()
			.map(translator -> CompletableFuture.supplyAsync(() -> {
				TranslatorResult translatorResult = new TranslatorResult();
				translatorResult.setName(translator.getName());
				try {
					translatorResult.setResult(translator.translate(msg.getContent()));
				} catch (Exception e) {
					translatorResult.setResult(e.getMessage());
				}
				return translatorResult;
			})).toList();

		String result;
		try {
			CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(3, TimeUnit.SECONDS);
			result = futures.stream().map(item -> {
				try {
					return item.get();
				} catch (InterruptedException | ExecutionException e) {
					return null;
				}
			}).filter(Objects::nonNull).map(TranslatorResult::toString).collect(Collectors.joining("\n\n"));
		} catch (Exception e) {
			result = e.getMessage();
		}
		platform.replyText(msg, result);
	}
}
