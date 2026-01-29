# 更新日志

本项目的所有重要更改都将记录在此文件中。

格式基于 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.0.0/)，
并且本项目遵循 [语义化版本](https://semver.org/lang/zh-CN/)。

## [Unreleased]

### 计划中
- JSON Schema 验证
- 更多数据类型支持
- 自定义主题配色

## [1.0.0] - 2025-01-29

### 新增
- JSON 格式化工具
  - JSON 美化（支持 2/4 空格缩进）
  - JSON 压缩（单行输出）
  - 语法校验 + 错误高亮
  - 一键复制结果

- 字节解码器
  - 十六进制 ↔ 二进制 ↔ 十进制互转
  - 位操作可视化（显示每个 bit）
  - 支持多种输入格式

- 字节解析器
  - 十六进制数据输入
  - 字节预览（偏移量 + 十六进制 + ASCII）
  - 自定义解析规则（按偏移量和长度）
  - 多种数据类型支持（Hex、String、Int8/16/32/64、UInt8/16/32、Float、Double）
  - 字节序支持（大端/小端）
  - 行号显示
  - 滚动预览（最多显示 10 行）

### 技术实现
- 使用 Kotlin + Jetpack Compose for Desktop (Jewel UI)
- 基于 IntelliJ Platform SDK 2025.2
- 支持 JDK 21+

[Unreleased]: https://github.com/yichangyiwai/idea-dev-tools/compare/v1.0.0...HEAD
[1.0.0]: https://github.com/yichangyiwai/idea-dev-tools/releases/tag/v1.0.0
