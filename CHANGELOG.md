# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2026-01-29

### Added

- **JSON 格式化工具**
  - 支持 JSON 美化，可配置 2/4 空格缩进
  - 支持 JSON 压缩为单行
  - 实时语法校验和错误提示
  - 一键复制功能

- **字节解码器**
  - 十六进制、二进制、十进制互转
  - 位级别可视化显示
  - 支持多种输入格式（0xFF、FF、0b11111111、255）

- **字节解析器**
  - 十六进制数据输入，支持多种格式
  - 字节预览功能，显示偏移量 + 十六进制 + ASCII
  - 字节预览支持滚动查看（最多显示 10 行）
  - 自定义解析规则（偏移量 + 字节数）
  - 支持多种数据类型：Hex、String、Int8/16/32/64、UInt8/16/32、Float、Double
  - 支持大端序/小端序切换
  - 解析规则类型选择器带有视觉选中反馈

### Technical

- 基于 Kotlin 开发
- 使用 Jetpack Compose for Desktop (Jewel UI)
- 支持 IntelliJ IDEA 2025.2+
- 需要 JDK 21+

## [Unreleased]

### Planned

- 更多数据类型支持
- 历史记录功能
- 更多实用工具
