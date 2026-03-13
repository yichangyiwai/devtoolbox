package com.yichangyiwai.devtoolbox.domain.byteparser

import com.yichangyiwai.devtoolbox.domain.hex.HexCodec
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset

object ByteParseService {
    fun parseAll(bytes: ByteArray, rules: List<ParseRule>, byteOrder: ByteOrder): List<ParseResult> =
        rules.mapIndexed { index, rule -> parseByRule(bytes, rule, index, byteOrder) }

    fun parseHexInput(input: String): Result<ByteArray> = HexCodec.parseHexString(input)

    fun parseByRule(bytes: ByteArray, rule: ParseRule, index: Int, byteOrder: ByteOrder): ParseResult {
        if (rule.offset < 0 || rule.offset >= bytes.size) {
            return ParseResult(error = "规则${index + 1}: 偏移量超出范围 (0-${bytes.size - 1})")
        }
        if (rule.length <= 0) {
            return ParseResult(error = "规则${index + 1}: 长度必须大于 0")
        }
        if (rule.offset + rule.length > bytes.size) {
            return ParseResult(error = "规则${index + 1}: 长度超出范围 (剩余 ${bytes.size - rule.offset} 字节)")
        }

        val subBytes = bytes.copyOfRange(rule.offset, rule.offset + rule.length)
        val buffer = ByteBuffer.wrap(subBytes).order(byteOrder)

        return try {
            val value = when (rule.type) {
                DataType.HEX -> HexCodec.formatHex(subBytes)
                DataType.INT8 -> if (subBytes.size >= 1) subBytes[0].toInt().toString() else "需要至少1字节"
                DataType.UINT8 -> if (subBytes.size >= 1) (subBytes[0].toInt() and 0xFF).toString() else "需要至少1字节"
                DataType.INT16 -> if (subBytes.size >= 2) buffer.short.toString() else "需要至少2字节"
                DataType.UINT16 -> if (subBytes.size >= 2) (buffer.short.toInt() and 0xFFFF).toString() else "需要至少2字节"
                DataType.INT32 -> if (subBytes.size >= 4) buffer.int.toString() else "需要至少4字节"
                DataType.UINT32 -> if (subBytes.size >= 4) (buffer.int.toLong() and 0xFFFFFFFFL).toString() else "需要至少4字节"
                DataType.INT64 -> if (subBytes.size >= 8) buffer.long.toString() else "需要至少8字节"
                DataType.FLOAT -> if (subBytes.size >= 4) buffer.float.toString() else "需要至少4字节"
                DataType.DOUBLE -> if (subBytes.size >= 8) buffer.double.toString() else "需要至少8字节"
                DataType.STRING -> String(subBytes, Charset.forName("UTF-8"))
            }
            ParseResult(value = value)
        } catch (e: Exception) {
            ParseResult(error = "规则${index + 1}: 解析错误: ${e.message}")
        }
    }
}
