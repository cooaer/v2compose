# App Store 上架 Checklist

适用项目：`V2compose` iOS 版本（`iosApp/`）

> 目标：把当前仓库整理到可提交 App Store Review 的状态。
> 使用方式：按顺序执行，完成后勾选。带“当前状态”说明的条目，是基于当前仓库扫描结果给出的判断。

---

## 1. 版本与构建号

- [x] 统一 iOS 版本号来源，避免 `Info.plist` 与 build settings 不一致
  - 当前状态：
    - `iosApp/Configuration/Base.xcconfig` 中为 `MARKETING_VERSION = 1.0.1`
    - `iosApp/Configuration/Base.xcconfig` 中为 `CURRENT_PROJECT_VERSION = 2`
    - `iosApp/iosApp/Info.plist` 中 `CFBundleShortVersionString` 使用 `$(MARKETING_VERSION)`
    - `iosApp/iosApp/Info.plist` 中 `CFBundleVersion` 使用 `$(CURRENT_PROJECT_VERSION)`
- [x] 确认本次发版版本号，例如 `1.0.1`
- [x] 确认本次发版 build 号，例如 `2`
- [ ] 确认 App Store Connect 中版本号与归档包一致

## 2. Bundle ID / 签名 / 能力配置

- [x] 确认 Release Bundle ID 已配置
  - `io.github.v2compose.iosApp`
- [x] 确认 Development Team 已配置
- [ ] 在 Apple Developer 后台确认：
  - [ ] App ID 已创建
  - [ ] 对应 Bundle ID 与本地工程一致
  - [ ] 自动签名或手动签名在 Release 下可正常工作
- [ ] 在真机或 Archive 过程中确认没有 provisioning/profile 错误

## 3. Firebase 与发布环境配置

- [x] 确认 iOS 已接入 Firebase Core / Analytics / Crashlytics
- [ ] 确认 Release 用的是正式 Firebase iOS App
  - 当前 Release 配置：`iosApp/Configuration/Release.xcconfig`
- [x] 确认 `iosApp/iosApp/GoogleService-Info.plist` 已存在
- [x] 确认 Release Archive 时配置了 `GoogleService-Info.plist` 拷贝脚本
- [ ] 确认 Crashlytics dSYM 上传脚本在 Archive 后可正常执行
- [x] 确认 Debug/Release 使用不同 Firebase plist 来源

## 4. 隐私与合规

- [x] 添加 app 级 `PrivacyInfo.xcprivacy`
  - 当前状态：`iosApp/iosApp/PrivacyInfo.xcprivacy` 已加入 iOS target Resources
- [x] 核对实际使用的权限与系统能力
  - [x] Photos Add Only
  - [x] Background Fetch / BGTaskScheduler
  - [x] Notifications
  - [x] Firebase Analytics / Crashlytics
- [x] 确认 `NSPhotoLibraryAddUsageDescription` 文案准确
- [ ] 如果后续新增权限，补充对应 usage description
- [ ] 准备并上线可公开访问的 Privacy Policy 页面
  - 当前状态：已准备 `docs/privacy-policy.md`，GitHub Pages URL 见 `docs/app-store-submission.md`，仍需确认 Pages 已部署且可访问
- [ ] 准备 Support URL 或支持邮箱页面
  - 当前状态：已准备 `docs/support.md`，仍需确认 Pages 已部署且可访问
- [x] 准备 Terms of Service 页面（建议）
- [ ] 在 App Store Connect 正确填写 App Privacy 问卷
  - [x] 基于 Firebase Analytics 实际采集项填写 usage data / identifiers 草稿
  - [x] 基于 Firebase Crashlytics 实际采集项填写 diagnostics 草稿
  - [x] 不要仅依据 README 中的口头描述填写

## 5. 审核敏感功能说明

- [x] 为审核准备 Review Notes
- [x] 在 Review Notes 中说明以下能力的用途：
  - [x] 自动签到
  - [x] 后台刷新 / BGTaskScheduler
  - [x] 通知权限用途
  - [x] 保存图片到相册用途
  - [x] Firebase 仅用于崩溃与基础统计
- [ ] 如果审核需要登录，准备测试账号与密码
- [x] 如果功能依赖特定环境或时机，补充复现步骤

## 6. 设备支持范围

- [x] 确认是否真的要支持 iPad
  - 当前状态：首版仅 iPhone，`TARGETED_DEVICE_FAMILY = 1`
- [x] 如果 **不支持 iPad**：改为仅 iPhone 并重新验证布局
- [ ] 如果 **支持 iPad**：
  - [ ] 补 iPad 截图
  - [ ] 验证 iPad 布局与交互
  - [ ] 确认 App Store Connect 素材齐全
- [x] 确认当前仅竖屏策略符合产品预期

## 7. 图标、截图与商店素材

- [ ] 确认 App Icon 在 Archive / 上传校验中无警告
  - 当前状态：`AppIcon.appiconset` 中仅看到 `1024x1024` 营销图标配置，需实际验证 Xcode/ASC 校验结果
- [x] 准备 App Store 截图
  - [x] 已有 phone screenshots
  - [x] 首版不保留 iPad 支持，因此不需要 iPad screenshots
- [x] 准备 App 名称
- [x] 准备副标题
- [x] 准备关键词
- [x] 准备中英文描述文案
- [x] 准备本次版本更新说明

## 8. App Store Connect 信息填写

- [ ] 创建 App Store Connect 应用条目
- [ ] 填写基础信息：
  - [ ] Name
  - [ ] Primary Language
  - [ ] Bundle ID
  - [ ] SKU
- [ ] 填写商店信息：
  - [ ] Description
  - [ ] Keywords
  - [ ] Support URL
  - [ ] Marketing URL（可选）
  - [ ] Privacy Policy URL
- [ ] 填写年龄分级
- [ ] 填写内容版权信息
- [ ] 填写 Export Compliance

## 9. 构建与归档验证

- [x] 在 Release 配置下本地构建通过
  - 当前状态：Release simulator `xcodebuild` 已通过，Archive 仍需单独验证
- [ ] 在 Xcode 中成功 Archive
- [ ] Validate App 通过
- [ ] 上传 App Store Connect 成功
- [ ] 检查上传后 Processing 无异常
- [ ] 检查符号文件与崩溃上报正常
- [ ] 检查启动、登录、浏览主题、发帖、回帖、消息、设置等主路径
- [ ] 检查以下边界场景：
  - [ ] 首次启动
  - [ ] 未登录使用
  - [ ] 登录失败
  - [ ] 网络异常
  - [ ] 权限被拒绝
  - [ ] 后台恢复

## 10. 提审前最终确认

- [x] 版本号 / build 号正确
- [ ] 隐私政策链接可访问
- [ ] 支持链接可访问
- [ ] App Privacy 已填写并复核
- [x] 截图与设备支持范围一致
- [x] Review Notes 已填写
- [ ] 测试账号已提供（如需要）
- [ ] Release Archive 已验证
- [ ] 上传包已完成处理
- [ ] 确认后提交审核

---

## 当前项目最优先处理项

建议按这个顺序推进：

1. [x] 统一版本号与 build 号
2. [x] 决定是否保留 iPad 支持
3. [x] 补 `PrivacyInfo.xcprivacy`
4. [ ] 准备 Privacy Policy / Support URL
5. [x] 填 App Privacy 问卷草稿
6. [ ] 做一次完整 Release Archive 验证
7. [ ] 提交 TestFlight / App Review
