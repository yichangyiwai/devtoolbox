# 贡献指南

感谢你考虑为 Dev Tools 插件做出贡献！

## 如何贡献

### 报告 Bug

如果你发现了 Bug，请在 GitHub Issues 中创建一个新的 Issue，并提供以下信息：

- **Bug 描述**：清晰简洁地描述 Bug
- **重现步骤**：详细列出重现 Bug 的步骤
- **期望行为**：描述你期望的正确行为
- **实际行为**：描述实际发生的行为
- **环境信息**：
  - IntelliJ IDEA 版本
  - 插件版本
  - 操作系统

### 提交新功能

如果你想添加新功能：

1. 先在 Issues 中创建一个功能请求，讨论实现方案
2. 等待维护者反馈后再开始开发
3. Fork 项目并创建功能分支
4. 实现功能并编写测试
5. 提交 Pull Request

### 开发环境设置

```bash
# 克隆仓库
git clone https://github.com/yichangyiwai/dev-tools.git
cd dev-tools

# 运行插件（启动沙盒 IDEA）
./gradlew runIde

# 构建插件
./gradlew buildPlugin
```

### 代码规范

- 使用 Kotlin 编写代码
- 遵循 Kotlin 官方代码风格
- 为新功能编写清晰的注释
- 保持代码简洁易读

### 提交规范

提交信息格式：`<type>(<scope>): <subject>`

**类型 (type)：**
- `feat`: 新功能
- `fix`: Bug 修复
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 重构
- `test`: 测试相关
- `chore`: 构建/工具相关

**示例：**
```
feat(json): 添加 JSON 压缩功能
fix(byte-parser): 修复字节序解析错误
docs(readme): 更新安装说明
```

### Pull Request 流程

1. Fork 项目
2. 创建功能分支：`git checkout -b feat/new-feature`
3. 提交更改：`git commit -m 'feat: 添加新功能'`
4. 推送到分支：`git push origin feat/new-feature`
5. 创建 Pull Request

### Pull Request 检查清单

在提交 PR 前，请确保：

- [ ] 代码可以正常编译
- [ ] 新功能已测试
- [ ] 更新了相关文档
- [ ] 遵循代码规范
- [ ] Commit 信息清晰明确

## 行为准则

- 尊重所有贡献者
- 接受建设性批评
- 关注项目最佳利益
- 展现同理心和友善

## 许可证

提交代码即表示你同意你的代码将以 MIT License 发布。

---

再次感谢你的贡献！🎉
