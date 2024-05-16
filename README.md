# msg-bot

自用的消息机器人, 支持扩展任意机器人/公众号等可以自动回复或消息通知的平台 (目前仅实现 ntchat)

## 使用

### 环境

jdk 21

### 配置

```bash
cp src/main/resources/application-example.yml src/main/resources/application.yml
# 并填写配置
```

### 构建

```bash
make build
```

### 启动

```bash
TZ=Asia/Shanghai java -jar msg-bot.jar
```
