package com.yichangyiwai.devtoolbox.domain.tcp

import com.yichangyiwai.devtoolbox.domain.byteparser.DataType
import com.yichangyiwai.devtoolbox.domain.hex.HexCodec
import java.nio.ByteBuffer

object TcpMessageBuilder {
    fun build(fields: List<TcpMessageField>): Result<ByteArray> {
        if (fields.isEmpty()) {
            return Result.failure(IllegalArgumentException("至少需要一个字段"))
        }
        val overlaps = mutableSetOf<Int>()
        val maxLength = fields.maxOf {
            if (it.offset < 0 || it.length <= 0) {
                throw IllegalArgumentException("偏移和长度必须合法")
            }
            it.offset + it.length
        }
        val occupied = BooleanArray(maxLength)
        val packet = ByteArray(maxLength)
        fields.forEachIndexed { index, field ->
            val bytes = fieldToBytes(field, index).getOrElse { return Result.failure(it) }
            if (bytes.size != field.length) {
                return Result.failure(IllegalArgumentException("字段${index + 1}: 生成字节数 ${bytes.size} 与配置长度 ${field.length} 不一致"))
            }
            for (i in field.offset until field.offset + field.length) {
                if (occupied[i]) overlaps += i
                occupied[i] = true
            }
            bytes.copyInto(packet, destinationOffset = field.offset)
        }
        if (overlaps.isNotEmpty()) {
            return Result.failure(IllegalArgumentException("字段偏移存在重叠，请调整配置"))
        }
        return Result.success(packet)
    }

    private fun fieldToBytes(field: TcpMessageField, index: Int): Result<ByteArray> {
        return try {
            when (field.inputMode) {
                ValueInputMode.HEX -> {
                    val bytes = HexCodec.parseHexString(field.inputValue).getOrElse { return Result.failure(IllegalArgumentException("字段${index + 1}: ${it.message}")) }
                    if (bytes.size != field.length) {
                        return Result.failure(IllegalArgumentException("字段${index + 1}: Hex 长度与配置长度不一致"))
                    }
                    Result.success(bytes)
                }
                ValueInputMode.NUMBER -> Result.success(numberToBytes(field, index))
            }
        } catch (e: Exception) {
            Result.failure(IllegalArgumentException("字段${index + 1}: ${e.message}"))
        }
    }

    private fun numberToBytes(field: TcpMessageField, index: Int): ByteArray {
        val buffer = ByteBuffer.allocate(field.length).order(field.byteOrder)
        return when (field.type) {
            DataType.INT8, DataType.UINT8 -> byteArrayOf(field.inputValue.toInt().toByte())
            DataType.INT16, DataType.UINT16 -> {
                require(field.length == 2) { "字段${index + 1}: Int16/UInt16 长度必须为2" }
                buffer.putShort(field.inputValue.toShort()).array()
            }
            DataType.INT32, DataType.UINT32 -> {
                require(field.length == 4) { "字段${index + 1}: Int32/UInt32 长度必须为4" }
                buffer.putInt(field.inputValue.toInt()).array()
            }
            DataType.INT64 -> {
                require(field.length == 8) { "字段${index + 1}: Int64 长度必须为8" }
                buffer.putLong(field.inputValue.toLong()).array()
            }
            DataType.FLOAT -> {
                require(field.length == 4) { "字段${index + 1}: Float 长度必须为4" }
                buffer.putFloat(field.inputValue.toFloat()).array()
            }
            DataType.DOUBLE -> {
                require(field.length == 8) { "字段${index + 1}: Double 长度必须为8" }
                buffer.putDouble(field.inputValue.toDouble()).array()
            }
            DataType.STRING -> {
                val bytes = field.inputValue.toByteArray()
                require(bytes.size == field.length) { "字段${index + 1}: 字符串字节长度必须等于配置长度" }
                bytes
            }
            DataType.HEX -> {
                val bytes = HexCodec.parseHexString(field.inputValue).getOrThrow()
                require(bytes.size == field.length) { "字段${index + 1}: Hex 长度与配置长度不一致" }
                bytes
            }
        }
    }
}
