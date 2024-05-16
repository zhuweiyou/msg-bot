# msg-bot

自用的消息机器人, 支持扩展任意机器人/公众号等可以自动回复或消息通知的平台

> 目前仅实现 ntchat, 可以实现 `Platform` 接口, 对接任意平台

## 开发环境

JDK 21

## 一键运行

不需要下载整个项目, 单独下载 [application.yml](src/main/resources/application.yml) 填好配置放到当前目录 `./application.yml`

```bash
docker run -d \
  -p 8080:8080 \
  -v ./application.yml:/app/config/application.yml \
  zhuweiyou/msg-bot:latest
```

## 对接平台

### ntchat 微信机器人

- 部署到 windows 看这里 [zhuweiyou/ntchat-0.1.13](https://github.com/zhuweiyou/ntchat-0.1.13)
- 部署到 linux 看这里 [zhuweiyou/ntchat-api](https://github.com/zhuweiyou/ntchat-api)

设置回调地址 `http://localhost:8080/ntchat/webhook`
