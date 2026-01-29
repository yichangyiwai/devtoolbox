# Contributing to Dev Tools

感谢你对 Dev Tools 项目的关注！我们欢迎各种形式的贡献。

## 如何贡献

### 报告 Bug

如果你发现了 bug，请 [创建一个 issue](https://github.com/yichangyiwai/idea-dev-tools/issues/new?template=bug_report.md)，并包含以下信息：

- Bug 的清晰描述
- 复现步骤
- 预期行为
- 实际行为
- 截图（如果适用）
- 环境信息（IDEA 版本、JDK 版本、操作系统）

### 提出新功能

如果你有好的想法，请 [创建一个 feature request](https://github.com/yichangyiwai/idea-dev-tools/issues/new?template=feature_request.md)，说明：

- 功能的详细描述
- 为什么需要这个功能
- 可能的实现方案

### 提交代码

1. **Fork 仓库**
   ```bash
   git clone https://github.com/yichangyiwai/idea-dev-tools.git
   cd idea-dev-tools
   ```

2. **创建分支**
   ```bash
   git checkout -b feature/your-feature-name
   ```

3. **开发和测试**
   - 确保代码符合项目的代码风格
   - 添加必要的测试
   - 运行 `./gradlew test` 确保所有测试通过
   - 运行 `./gradlew runIde` 进行手动测试

4. **提交更改**
   ```bash
   git add .
   git commit -m "feat: add your feature description"
   ```

   提交信息格式：
   - `feat:` - 新功能
   - `fix:` - Bug 修复
   - `docs:` - 文档更新
   - `refactor:` - 代码重构
   - `test:` - 测试相关
   - `chore:` - 构建/工具相关

5. **推送并创建 PR**
   ```bash
   git push origin feature/your-feature-name
   ```
   然后在 GitHub 上创建 Pull Request

## 开发环境设置

### 要求

- IntelliJ IDEA 2025.2+
- JDK 21+
- Gradle 9.0+

### 构建项目

```bash
./gradlew clean buildPlugin
```

### 运行插件

```bash
./gradlew runIde
```

### 代码风格

- 使用 Kotlin 官方代码风格
- 4 空格缩进
- 类名使用 PascalCase
- 函数名使用 camelCase
- 常量使用 UPPER_SNAKE_CASE

### 项目结构

```
src/main/kotlin/com/yichangyiwai/devtools/
├── DevToolsWindowFactory.kt      # 工具窗口入口
└── ui/
    ├── JsonFormatterPanel.kt     # JSON 格式化面板
    ├── ByteDecoderPanel.kt       # 字节解码器面板
    └── ByteParserPanel.kt        # 字节解析器面板
```

## Pull Request 检查清单

在提交 PR 之前，请确保：

- [ ] 代码已经过测试
- [ ] 所有测试通过
- [ ] 代码符合项目风格
- [ ] 更新了相关文档
- [ ] 更新了 CHANGELOG.md（如果是用户可见的更改）
- [ ] PR 描述清晰说明了更改内容

## 社区准则

- 尊重所有贡献者
- 保持友好和建设性的讨论
- 接受不同的观点和经验水平

## 许可证

提交代码即表示你同意将你的贡献以 MIT 许可证发布。

## 问题？

如有任何问题，请通过以下方式联系：

- GitHub Issues: https://github.com/yichangyiwai/idea-dev-tools/issues
- Email: 通过 GitHub profile 联系

感谢你的贡献！🎉
