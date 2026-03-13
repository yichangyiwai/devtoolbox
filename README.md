# DevToolbox - IntelliJ IDEA 开发者工具箱插件

[![JetBrains Plugin](https://img.shields.io/badge/JetBrains-Plugin-blue.svg)](https://plugins.jetbrains.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![IDEA Version](https://img.shields.io/badge/IDEA-2025.2%2B-orange.svg)](https://www.jetbrains.com/idea/)
[![JDK Version](https://img.shields.io/badge/JDK-21%2B-green.svg)](https://openjdk.org/)

一个功能强大的 IntelliJ IDEA 插件，为日常开发提供实用工具集。

## ✨ 功能特性

### 1. JSON 格式化工具

- **美化** - 格式化 JSON，支持 2/4 空格缩进配置
- **压缩** - 将 JSON 压缩为单行输出
- **校验** - 实时语法校验 + 错误提示
- **复制** - 一键复制结果到剪贴板

### 2. 字节解码器

- **进制转换** - 十六进制 ↔ 二进制 ↔ 十进制 互转
- **位可视化** - 每个 bit 可视化显示，按字节分组
- 支持多种输入格式：`0xFF`、`FF`、`0b11111111`、`255`

### 3. 字节解析器

- **十六进制输入** - 支持多种格式：`48 65 6C 6C 6F`、`0x48656C6C6F`
- **字节预览** - 显示偏移量 + 十六进制 + ASCII 对照表（最多显示 10 行，支持滚动）
- **自定义解析规则** - 按偏移量和长度解析数据
- **多种数据类型** - Hex、String、Int8/16/32/64、UInt8/16/32、Float、Double
- **字节序支持** - 大端序 (Big Endian) / 小端序 (Little Endian)

### 4. TCP 发送面板

- **TCP 连接** - 连接指定主机与端口进行报文发送
- **完整 Hex 直发** - 直接输入完整十六进制报文并校验
- **规则组包** - 按偏移、长度、类型、字节序组装最终 TCP 报文
- **发送预览与日志** - 发送前预览完整报文，并记录实际发送内容

### 5. TCP 接收解析面板

- **TCP 监听** - 监听指定主机与端口并接收消息
- **接收记录** - 显示时间戳、来源地址、字节长度与完整原始 Hex
- **实时解析** - 对当前选中接收记录按规则实时解析并行内显示结果
- **Ping 探活** - 在接收面板向已连接客户端发送可配置 Ping 消息

## 📋 环境要求

- **IntelliJ IDEA** 2025.2+
- **JDK** 21+

## 🚀 安装

### 从 JetBrains Marketplace 安装（推荐）

1. 打开 IDEA → `File` → `Settings` → `Plugins`
2. 搜索 "DevToolbox"
3. 点击 `Install` → 重启 IDEA

### 手动安装

1. 从 [Releases](https://github.com/yichangyiwai/devtoolbox/releases) 下载最新版本的 `.zip` 文件
2. 打开 IDEA → `File` → `Settings` → `Plugins`
3. 点击 ⚙️ → `Install Plugin from Disk...`
4. 选择下载的 `.zip` 文件 → 重启 IDEA

## 📖 使用方法

安装后，在 IDEA 右侧工具栏找到 **"DevToolbox"** 图标，点击打开工具窗口，包含五个 Tab：

- **JSON 格式化** - JSON 美化、压缩、校验
- **字节解码器** - 进制转换、位可视化
- **字节解析器** - 按规则解析字节数据
- **TCP 发送** - TCP 连接、规则组包、完整 Hex 发送
- **TCP 接收解析** - 接收记录、Ping 探活、规则实时解析

## 🛠️ 开发

### 构建插件

```bash
./gradlew clean buildPlugin
```

构建完成后，插件文件位于：`build/distributions/devtoolbox-1.2.0.zip`

### 开发调试

```bash
./gradlew runIde
```

### 运行测试

```bash
./gradlew test
```

## 📦 项目结构

```
src/main/kotlin/com/yichangyiwai/devtools/
├── DevToolsWindowFactory.kt      # 主窗口工厂
├── MyMessageBundle.kt            # 国际化资源
└── ui/
    ├── JsonFormatterPanel.kt     # JSON 格式化工具
    ├── ByteDecoderPanel.kt       # 字节解码器（进制转换）
    └── ByteParserPanel.kt        # 字节解析器
```

## 🔧 技术栈

- **Kotlin** - 主要编程语言
- **Jetpack Compose for Desktop (Jewel UI)** - 现代 UI 框架
- **IntelliJ Platform SDK** - 插件开发框架
- **Gradle** - 构建工具

## 🤝 贡献

欢迎贡献！请查看 [贡献指南](CONTRIBUTING.md)。

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📝 更新日志

查看 [CHANGELOG.md](CHANGELOG.md) 了解版本历史。

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 📧 联系方式

- GitHub: [@yichangyiwai](https://github.com/yichangyiwai)
- Issues: [GitHub Issues](https://github.com/yichangyiwai/devtoolbox/issues)

## ⭐ Star History

如果这个项目对你有帮助，请给它一个 ⭐️！
