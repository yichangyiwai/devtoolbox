# Dev Tools - IntelliJ IDEA 开发者工具插件

<div align="center">

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Build](https://img.shields.io/badge/build-passing-brightgreen.svg)]()
[![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)]()
[![IntelliJ IDEA](https://img.shields.io/badge/IntelliJ%20IDEA-2025.2+-orange.svg)](https://www.jetbrains.com/idea/)

一个实用的 IntelliJ IDEA 插件，提供常用的开发辅助工具。

[功能特性](#功能特性) • [安装](#构建与安装) • [使用](#使用方法) • [贡献](#贡献) • [许可证](#许可证)

</div>

---

## ✨ 功能特性

### 1️⃣ JSON 格式化工具

- ✅ **美化** - 格式化 JSON，支持 2/4 空格缩进配置
- ✅ **压缩** - 将 JSON 压缩为单行输出
- ✅ **校验** - 语法校验 + 错误提示
- ✅ **复制** - 一键复制结果到剪贴板

### 2️⃣ 字节解码器

- ✅ **进制转换** - 十六进制 ↔ 二进制 ↔ 十进制 互转
- ✅ **位可视化** - 每个 bit 可视化显示，按字节分组
- ✅ 支持多种输入格式：`0xFF`、`FF`、`0b11111111`、`255`

### 3️⃣ 字节解析器

- ✅ **十六进制输入** - 支持多种格式：`48 65 6C 6C 6F`、`0x48656C6C6F`
- ✅ **字节预览** - 显示行号 + 偏移量 + 十六进制 + ASCII 对照表
- ✅ **自定义解析规则** - 按偏移量和长度解析数据
- ✅ **多种数据类型** - Hex、String、Int8/16/32/64、UInt8/16/32、Float、Double
- ✅ **字节序支持** - 大端 (Big Endian) / 小端 (Little Endian)
- ✅ **滚动预览** - 限制 10 行显示，长数据可滚动查看

---

## 📋 环境要求

- IntelliJ IDEA 2025.2+
- JDK 21+

---

## 📁 项目结构

```
src/main/kotlin/com/yichangyiwai/devtools/
├── DevToolsWindowFactory.kt      # 主窗口工厂
└── ui/
    ├── JsonFormatterPanel.kt     # JSON 格式化工具
    ├── ByteDecoderPanel.kt       # 字节解码器（进制转换）
    └── ByteParserPanel.kt        # 字节解析器
```

---

## 🚀 构建与安装

### 方式一：从源码构建

```bash
# 克隆项目
git clone https://github.com/yichangyiwai/idea-dev-tools.git
cd idea-dev-tools

# 构建插件
./gradlew clean buildPlugin
```

构建完成后，插件文件位于：`build/distributions/idea-dev-tools-1.0-SNAPSHOT.zip`

### 方式二：开发调试

```bash
./gradlew runIde
```

### 安装到 IDEA

1. 打开 IDEA → `File` → `Settings` (或 `Ctrl+Alt+S`)
2. 左侧选择 `Plugins`
3. 点击右上角 ⚙️ 齿轮图标 → `Install Plugin from Disk...`
4. 选择 `build/distributions/idea-dev-tools-1.0-SNAPSHOT.zip`
5. 点击 `OK` → 重启 IDEA

---

## 📖 使用方法

安装后，在 IDEA 右侧工具栏找到 **"idea-dev-tools"**，包含三个 Tab：

| 功能 | 说明 |
|------|------|
| **JSON 格式化** | JSON 美化、压缩、语法校验 |
| **字节解码器** | 进制转换、位操作可视化 |
| **字节解析器** | 按规则解析字节数据 |

---

## 🛠️ 技术栈

- **语言**: Kotlin
- **UI 框架**: Jetpack Compose for Desktop (Jewel UI)
- **平台**: IntelliJ Platform SDK 2025.2
- **构建工具**: Gradle 8.x
- **JDK**: 21

---

## 🤝 贡献

欢迎贡献！请查看 [CONTRIBUTING.md](CONTRIBUTING.md) 了解详情。

### 贡献者

感谢所有贡献者的付出！

---

## 📝 更新日志

查看 [CHANGELOG.md](CHANGELOG.md) 了解版本更新历史。

---

## 📄 许可证

本项目采用 [MIT License](LICENSE) 开源协议。

---

## 💬 联系方式

- **Issues**: [GitHub Issues](https://github.com/yichangyiwai/idea-dev-tools/issues)
- **作者**: yichangyiwai

---

<div align="center">

**如果觉得这个项目对你有帮助，请给个 ⭐ Star 吧！**

Made with ❤️ by yichangyiwai

</div>
