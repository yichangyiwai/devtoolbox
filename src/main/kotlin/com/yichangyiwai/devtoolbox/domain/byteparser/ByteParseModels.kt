package com.yichangyiwai.devtoolbox.domain.byteparser

import java.nio.ByteOrder

data class ParseRule(
    val offset: Int = 0,
    val length: Int = 1,
    val type: DataType = DataType.HEX,
    val radix: Int = 10,
)

data class ParseResult(
    val value: String? = null,
    val error: String? = null,
)

enum class DataType(val displayName: String) {
    HEX("Hex"),
    STRING("String"),
    INT8("Int8"),
    UINT8("UInt8"),
    INT16("Int16"),
    UINT16("UInt16"),
    INT32("Int32"),
    UINT32("UInt32"),
    INT64("Int64"),
    FLOAT("Float"),
    DOUBLE("Double")
}

data class ByteParseContext(
    val rules: List<ParseRule> = listOf(ParseRule()),
    val byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN,
)
