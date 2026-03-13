package com.yichangyiwai.devtoolbox.domain.hex

object HexCodec {
    fun parseHexString(input: String): Result<ByteArray> {
        val cleaned = normalizeHex(input)
        if (cleaned.isEmpty()) {
            return Result.failure(IllegalArgumentException("输入为空"))
        }
        if (cleaned.length % 2 != 0) {
            return Result.failure(IllegalArgumentException("十六进制长度必须为偶数"))
        }
        if (!cleaned.all { it.isDigit() || it.lowercaseChar() in 'a'..'f' }) {
            return Result.failure(IllegalArgumentException("无效的十六进制格式"))
        }
        return try {
            Result.success(cleaned.chunked(2).map { it.toInt(16).toByte() }.toByteArray())
        } catch (_: Exception) {
            Result.failure(IllegalArgumentException("无效的十六进制格式"))
        }
    }

    fun normalizeHex(input: String): String = input.trim()
        .replace("0x", "", ignoreCase = true)
        .replace(" ", "")
        .replace(",", "")
        .replace("-", "")
        .replace("\n", "")
        .replace("\r", "")
        .replace("\t", "")
        .uppercase()

    fun formatHex(bytes: ByteArray, separator: String = " "): String =
        bytes.joinToString(separator) { "%02X".format(it) }
}
