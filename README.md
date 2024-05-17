# msg-bot

自用的消息机器人, 支持扩展任意机器人/公众号等可以自动回复或消息通知的平台

## 开发环境

JDK 21

## 一键运行

拷贝 [application.yml](src/main/resources/application.yml)
填好配置放到当前目录 `./application.yml`

```bash
# 如果你不修改代码 不需要这一步
docker build -t zhuweiyou/msg-bot:latest .

# 运行
docker run -d \
    -p 8080:8080 \
    -v ./application.yml:/app/config/application.yml \
    zhuweiyou/msg-bot:latest
```

> 上面的 `:latest` 支持分支名或版本号, 比如 `:main`, `:1.0.0` 等

## 对接平台

### wxbot 微信(v3.9.8.25)机器人

部署看这里 [jwping/wxbot](https://github.com/jwping/wxbot)

#### wxbot.json

```json
{
    "addr": "0.0.0.0:18080",
    "sync-url": {
        "general-msg": [
            {
                "timeout": 3000,
                "url": "http://localhost:8080/wxbot/webhook"
            }
        ]
    }
}
```

### ntchat 微信(v3.6.0.18)机器人

> 部分微信号已经被微信限制登录 3.6 版本, 可以考虑使用上面的 wxbot

- 部署到 windows 看这里 [zhuweiyou/ntchat-0.1.13](https://github.com/zhuweiyou/ntchat-0.1.13)
- 部署到 linux 看这里 [zhuweiyou/ntchat-api](https://github.com/zhuweiyou/ntchat-api)

设置回调地址 `http://localhost:8080/ntchat/webhook`
