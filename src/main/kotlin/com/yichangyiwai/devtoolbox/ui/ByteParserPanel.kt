package com.yichangyiwai.devtoolbox.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.jewel.ui.component.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset

@Composable
fun ByteParserPanel() {
    var hexInput by remember { mutableStateOf("") }
    var parsedBytes by remember { mutableStateOf<ByteArray?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var parseRules by remember { mutableStateOf(listOf(ParseRule())) }
    var parseResults by remember { mutableStateOf<List<ParseResult>>(emptyList()) }
    var byteOrder by remember { mutableStateOf(ByteOrder.BIG_ENDIAN) }

    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 输入区域
        Column {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("十六进制数据:", fontSize = 12.sp)
                Spacer(Modifier.weight(1f))
                Text("字节序:", fontSize = 11.sp, color = Color.Gray)
                // 大端按钮
                Box(
                    modifier = Modifier
                        .height(28.dp)
                        .background(
                            if (byteOrder == ByteOrder.BIG_ENDIAN) Color(0xFF4A6DA7) else Color.Transparent,
                            RoundedCornerShape(4.dp)
                        )
                        .border(
                            1.dp,
                            if (byteOrder == ByteOrder.BIG_ENDIAN) Color(0xFF6A9FD9) else Color.Gray.copy(alpha = 0.5f),
                            RoundedCornerShape(4.dp)
                        )
                        .clickable { byteOrder = ByteOrder.BIG_ENDIAN }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "大端",
                        fontSize = 12.sp,
                        color = if (byteOrder == ByteOrder.BIG_ENDIAN) Color.White else Color(0xFFA9B7C6)
                    )
                }
                // 小端按钮
                Box(
                    modifier = Modifier
                        .height(28.dp)
                        .background(
                            if (byteOrder == ByteOrder.LITTLE_ENDIAN) Color(0xFF4A6DA7) else Color.Transparent,
                            RoundedCornerShape(4.dp)
                        )
                        .border(
                            1.dp,
                            if (byteOrder == ByteOrder.LITTLE_ENDIAN) Color(0xFF6A9FD9) else Color.Gray.copy(alpha = 0.5f),
                            RoundedCornerShape(4.dp)
                        )
                        .clickable { byteOrder = ByteOrder.LITTLE_ENDIAN }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "小端",
                        fontSize = 12.sp,
                        color = if (byteOrder == ByteOrder.LITTLE_ENDIAN) Color.White else Color(0xFFA9B7C6)
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
            HexInputField(
                value = hexInput,
                onValueChange = { hexInput = it },
                modifier = Modifier.fillMaxWidth().height(80.dp)
            )
        }

        // 解析按钮
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DefaultButton(onClick = {
                val result = parseHexString(hexInput)
                if (result.isSuccess) {
                    parsedBytes = result.getOrNull()
                    errorMessage = null
                } else {
                    errorMessage = result.exceptionOrNull()?.message
                    parsedBytes = null
                }
            }) { Text("解析十六进制") }

            DefaultButton(onClick = {
                parseRules = parseRules + ParseRule()
            }) { Text("+ 添加规则") }

            DefaultButton(onClick = {
                parsedBytes?.let { bytes ->
                    parseResults = parseRules.mapIndexed { index, rule ->
                        parseByRule(bytes, rule, index, byteOrder)
                    }
                }
            }) { Text("执行解析") }

            DefaultButton(onClick = {
                parseRules = listOf(ParseRule())
                parseResults = emptyList()
            }) { Text("清空规则") }
        }

        // 错误提示
        errorMessage?.let { error ->
            Text(
                text = "❌ $error",
                color = Color(0xFFE53935),
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x20E53935), RoundedCornerShape(4.dp))
                    .padding(8.dp)
            )
        }

        // 字节预览
        parsedBytes?.let { bytes ->
            BytePreview(bytes)
        }

        // 解析规则列表
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("解析规则:", fontSize = 12.sp, fontWeight = FontWeight.Bold)

            parseRules.forEachIndexed { index, rule ->
                ParseRuleRow(
                    index = index,
                    rule = rule,
                    result = parseResults.getOrNull(index),
                    onUpdate = { newRule ->
                        parseRules = parseRules.toMutableList().apply { this[index] = newRule }
                    },
                    onDelete = {
                        if (parseRules.size > 1) {
                            parseRules = parseRules.toMutableList().apply { removeAt(index) }
                            parseResults = emptyList()
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun HexInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .border(1.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
            .background(Color(0xFF2B2B2B), RoundedCornerShape(4.dp))
            .padding(8.dp)
    ) {
        if (value.isEmpty()) {
            Text(
                text = "输入十六进制数据，如: 48 65 6C 6C 6F 或 0x48656C6C6F",
                color = Color.Gray.copy(alpha = 0.5f),
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                color = Color(0xFFA9B7C6),
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace
            ),
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun BytePreview(bytes: ByteArray) {
    val scrollState = rememberScrollState()
    val rowCount = (bytes.size + 7) / 8  // 每行8字节
    val maxVisibleRows = 10

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF3C3F41), RoundedCornerShape(4.dp))
            .padding(8.dp)
    ) {
        Text("字节预览 (共 ${bytes.size} 字节, ${rowCount} 行):", fontSize = 11.sp, color = Color.Gray)
        Spacer(Modifier.height(4.dp))

        // 偏移量 + 十六进制 + ASCII
        Row(modifier = Modifier.fillMaxWidth()) {
            // 行号标题
            Text("行", fontSize = 10.sp, color = Color.Gray, modifier = Modifier.width(30.dp))
            // 偏移量标题
            Text("偏移", fontSize = 10.sp, color = Color.Gray, modifier = Modifier.width(40.dp))
            // 十六进制标题
            Text("十六进制", fontSize = 10.sp, color = Color.Gray, modifier = Modifier.width(200.dp))
            // ASCII 标题
            Text("ASCII", fontSize = 10.sp, color = Color.Gray)
        }

        // 限制最大高度，超出滚动
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = (maxVisibleRows * 20).dp)  // 每行约20dp
                .verticalScroll(scrollState)
        ) {
            bytes.toList().chunked(8).forEachIndexed { rowIndex, rowBytes ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                    // 行号
                    Text(
                        String.format("%d", rowIndex + 1),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color.Gray,
                        modifier = Modifier.width(30.dp)
                    )
                    // 偏移量
                    Text(
                        String.format("%04X", rowIndex * 8),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF6897BB),
                        modifier = Modifier.width(40.dp)
                    )
                    // 十六进制
                    Text(
                        rowBytes.joinToString(" ") { String.format("%02X", it) },
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF6A8759),
                        modifier = Modifier.width(200.dp)
                    )
                    // ASCII
                    Text(
                        rowBytes.map { if (it in 32..126) it.toInt().toChar() else '.' }.joinToString(""),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFFCC7832)
                    )
                }
            }
        }
    }
}

@Composable
private fun ParseRuleRow(
    index: Int,
    rule: ParseRule,
    result: ParseResult?,
    onUpdate: (ParseRule) -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF3C3F41), RoundedCornerShape(4.dp))
            .padding(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("#${index + 1}", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.width(24.dp))

            // 偏移量
            Column {
                Text("偏移", fontSize = 9.sp, color = Color.Gray)
                SmallTextField(
                    value = rule.offset.toString(),
                    onValueChange = { onUpdate(rule.copy(offset = it.toIntOrNull() ?: 0)) },
                    modifier = Modifier.width(50.dp)
                )
            }

            // 长度
            Column {
                Text("长度", fontSize = 9.sp, color = Color.Gray)
                SmallTextField(
                    value = rule.length.toString(),
                    onValueChange = { onUpdate(rule.copy(length = it.toIntOrNull() ?: 1)) },
                    modifier = Modifier.width(50.dp)
                )
            }

            // 类型
            Column {
                Text("类型", fontSize = 9.sp, color = Color.Gray)
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    DataType.entries.forEach { type ->
                        val isSelected = rule.type == type
                        Box(
                            modifier = Modifier
                                .height(24.dp)
                                .background(
                                    if (isSelected) Color(0xFF4A6DA7) else Color.Transparent,
                                    RoundedCornerShape(4.dp)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) Color(0xFF6A9FD9) else Color.Gray.copy(alpha = 0.5f),
                                    RoundedCornerShape(4.dp)
                                )
                                .clickable { onUpdate(rule.copy(type = type)) }
                                .padding(horizontal = 6.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                type.displayName,
                                fontSize = 10.sp,
                                color = if (isSelected) Color.White else Color(0xFFA9B7C6)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            // 删除按钮
            OutlinedButton(onClick = onDelete) {
                Text("×", fontSize = 14.sp)
            }
        }

        // 解析结果
        result?.let { res ->
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (res.error != null) Color(0x20E53935) else Color(0x206A8759),
                        RoundedCornerShape(2.dp)
                    )
                    .padding(4.dp)
            ) {
                if (res.error != null) {
                    Text("❌ ${res.error}", fontSize = 11.sp, color = Color(0xFFE53935))
                } else {
                    Text(
                        "→ ${res.value}",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF6A8759)
                    )
                }
            }
        }
    }
}

@Composable
private fun SmallTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(24.dp)
            .border(1.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(2.dp))
            .background(Color(0xFF2B2B2B), RoundedCornerShape(2.dp))
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                color = Color(0xFFA9B7C6),
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            ),
            singleLine = true,
            modifier = Modifier.fillMaxSize()
        )
    }
}

private data class ParseRule(
    val offset: Int = 0,
    val length: Int = 1,
    val type: DataType = DataType.HEX
)

private data class ParseResult(
    val value: String? = null,
    val error: String? = null
)

private enum class DataType(val displayName: String) {
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

private fun parseHexString(input: String): Result<ByteArray> {
    val cleaned = input.trim()
        .replace("0x", "")
        .replace("0X", "")
        .replace(" ", "")
        .replace(",", "")
        .replace("-", "")

    if (cleaned.isEmpty()) {
        return Result.failure(IllegalArgumentException("输入为空"))
    }

    if (cleaned.length % 2 != 0) {
        return Result.failure(IllegalArgumentException("十六进制长度必须为偶数"))
    }

    return try {
        val bytes = cleaned.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
        Result.success(bytes)
    } catch (e: Exception) {
        Result.failure(IllegalArgumentException("无效的十六进制格式"))
    }
}

private fun parseByRule(bytes: ByteArray, rule: ParseRule, index: Int, byteOrder: ByteOrder): ParseResult {
    if (rule.offset < 0 || rule.offset >= bytes.size) {
        return ParseResult(error = "偏移量超出范围 (0-${bytes.size - 1})")
    }

    if (rule.offset + rule.length > bytes.size) {
        return ParseResult(error = "长度超出范围 (剩余 ${bytes.size - rule.offset} 字节)")
    }

    val subBytes = bytes.copyOfRange(rule.offset, rule.offset + rule.length)
    val buffer = ByteBuffer.wrap(subBytes).order(byteOrder)

    return try {
        val value = when (rule.type) {
            DataType.HEX -> subBytes.joinToString(" ") { String.format("%02X", it) }
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
        ParseResult(error = "解析错误: ${e.message}")
    }
}
