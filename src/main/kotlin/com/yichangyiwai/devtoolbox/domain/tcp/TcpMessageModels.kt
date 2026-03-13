package com.yichangyiwai.devtoolbox.domain.tcp

import com.yichangyiwai.devtoolbox.domain.byteparser.DataType
import java.nio.ByteOrder

enum class TcpSendMode(val displayName: String) {
    HEX("完整 Hex"),
    RULES("规则组装")
}

enum class ValueInputMode(val displayName: String) {
    NUMBER("数值"),
    HEX("Hex")
}

data class TcpMessageField(
    val offset: Int = 0,
    val length: Int = 1,
    val type: DataType = DataType.UINT8,
    val inputMode: ValueInputMode = ValueInputMode.NUMBER,
    val inputValue: String = "0",
    val byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
)

data class TcpReceivedMessage(
    val timestamp: String,
    val remoteAddress: String,
    val data: ByteArray,
)
