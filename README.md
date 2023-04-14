# V2Compose

一个 Material You 风格的 V2ex Android 客户端！完全使用 Compose 构建 UI！

该项目是一个练手项目，欢迎大家使用！如有发现体验的问题或者对 Android、Compose 使用不正确的地方，欢迎大家的批评和指正！

该项目已具备V站的大多的日常功能，如果对功能有想法，欢迎提交 pull request 或者 issues。

## 项目特色

* UI 部分完全使用 Compose 实现；
* 简单实现了一个 HtmlText 组件，用于渲染主题和回复中的 Html，相对嵌入一个 WebView 来说效率更高；

## 一些页面预览

| ![news](https://raw.githubusercontent.com/cooaer/v2compose/master/metadata/en-US/images/phoneScreenshots/1.jpg)  | ![topic](https://raw.githubusercontent.com/cooaer/v2compose/master/metadata/en-US/images/phoneScreenshots/2.jpg) | ![nodes](https://raw.githubusercontent.com/cooaer/v2compose/master/metadata/en-US/images/phoneScreenshots/3.jpg)    |
|------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------|
| ![write](https://raw.githubusercontent.com/cooaer/v2compose/master/metadata/en-US/images/phoneScreenshots/4.jpg) | ![mine](https://raw.githubusercontent.com/cooaer/v2compose/master/metadata/en-US/images/phoneScreenshots/5.jpg)  | ![settings](https://raw.githubusercontent.com/cooaer/v2compose/master/metadata/en-US/images/phoneScreenshots/6.jpg) |

## 已完成功能

- [x] 主页：展示一些推荐节点的最新的主题；
- [x] 节点导航页面：展示一些常用的节点；
- [x] 搜索页面：支持搜索话题；
- [x] 主题详情页面：展示主题的详细信息、评论列表；
- [x] 节点详情页面：展示节点的详细信息、该节点下最近回复的主题；
- [x] 用户信息页面：展示用户详细信息、用户创建的主题列表、用户的回复列表；
- [x] 我的页面；
- [x] 设置页面：支持一些简单的设置项；
- [x] 登录；
- [x] 消息通知；
- [x] 创建主题；
- [x] 创建回复；
- [x] 收藏主题，忽略主题，感谢主题，感谢回复，忽略回复；
- [x] 我的节点收藏，我的主题收藏，我的特别关注；

### 特色功能：

- [x] 自动签到；
- [x] 动态主题；
- [x] 代理服务器；
- [x] 高亮OP的回帖；
- [x] 可点击回复中的楼层；
- [x] 主页底部Tab，单击：回到顶部，双击：回到顶部并刷新；

## 用户隐私

V2compose 集成了 Firebase 的 Crashlytics 和 Analytics 功能，仅用于收集崩溃信息、统计基本的应用活跃信息，没有收集任何用户隐私相关的数据。请放心使用。

## 构建须知

V2compose 集成了 Firebase，开放的源码中不包含 Firebase 的密钥。如果你需要构建自己的 V2compose 版本，请创建自己 Firebase 应用，添加
google-services.json 至 app 目录下。

## 特别感谢

* [V2er-app/Android](https://github.com/v2er-app/Android) : 一个 Android 端的 V2ex 客户端，V2Compose
  使用该项目的部分网络相关代码！
* [sov2ex](https://github.com/Bynil/sov2ex) : 一个便捷的 V2EX 站内搜索引擎，V2Compose 使用该项目的 API 实现搜索 V2ex 的功能！

## JetBrains support

Thanks to JetBrains for supporting open source projects.

<a href="https://jb.gg/OpenSourceSupport" target="_blank">https://jb.gg/OpenSourceSupport.</a>