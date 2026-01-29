package com.yichangyiwai.devtoolbox.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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

@Composable
fun ByteDecoderPanel() {
    var inputText by remember { mutableStateOf("") }
    var inputType by remember { mutableStateOf(InputType.HEX) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var conversionResult by remember { mutableStateOf<ConversionResult?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 输入类型选择
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("输入类型:", fontSize = 12.sp)
            InputType.entries.forEach { type ->
                val isSelected = inputType == type
                if (isSelected) {
                    DefaultButton(
                        onClick = { }
                    ) {
                        Text(type.displayName)
                    }
                } else {
                    OutlinedButton(
                        onClick = {
                            inputType = type
                            inputText = ""
                            conversionResult = null
                            errorMessage = null
                        }
                    ) {
                        Text(type.displayName)
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            DefaultButton(onClick = {
                val result = convert(inputText, inputType)
                if (result.isSuccess) {
                    conversionResult = result.getOrNull()
                    errorMessage = null
                } else {
                    errorMessage = result.exceptionOrNull()?.message
                    conversionResult = null
                }
            }) { Text("转换") }

            DefaultButton(onClick = {
                inputText = ""
                conversionResult = null
                errorMessage = null
            }) { Text("清空") }
        }

        // 输入区域
        Column {
            Text("输入 (${inputType.displayName}):", fontSize = 12.sp)
            Spacer(Modifier.height(4.dp))
            InputTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = inputType.placeholder,
                modifier = Modifier.fillMaxWidth().height(60.dp)
            )
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

        // 转换结果
        conversionResult?.let { result ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ResultCard("十进制 (Decimal)", result.decimal)
                ResultCard("十六进制 (Hex)", result.hex)
                ResultCard("二进制 (Binary)", result.binary)

                // 位可视化
                BitVisualization(result.bits)
            }
        }
    }
}

@Composable
private fun InputTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
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
                text = placeholder,
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
private fun ResultCard(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF3C3F41), RoundedCornerShape(4.dp))
            .padding(12.dp)
    ) {
        Text(label, fontSize = 11.sp, color = Color.Gray)
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 14.sp,
            fontFamily = FontFamily.Monospace,
            color = Color(0xFF6A8759)
        )
    }
}

@Composable
private fun BitVisualization(bits: List<Int>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF3C3F41), RoundedCornerShape(4.dp))
            .padding(12.dp)
    ) {
        Text("位可视化 (Bit Visualization)", fontSize = 11.sp, color = Color.Gray)
        Spacer(Modifier.height(8.dp))

        // 按字节分组显示
        bits.chunked(8).forEachIndexed { byteIndex, byteBits ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text(
                    "Byte $byteIndex:",
                    fontSize = 10.sp,
                    color = Color.Gray,
                    modifier = Modifier.width(50.dp)
                )

                byteBits.forEachIndexed { bitIndex, bit ->
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                if (bit == 1) Color(0xFF6A8759) else Color(0xFF4E4E4E),
                                RoundedCornerShape(2.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = bit.toString(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (bit == 1) Color.White else Color.Gray
                        )
                    }
                }

                Spacer(Modifier.width(8.dp))

                // 位索引标注
                Text(
                    "[${7 - 0}..${7 - 7}]",
                    fontSize = 9.sp,
                    color = Color.Gray.copy(alpha = 0.6f)
                )
            }
        }

        // 位索引说明
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            Text("位索引:", fontSize = 10.sp, color = Color.Gray, modifier = Modifier.width(50.dp))
            (7 downTo 0).forEach { idx ->
                Box(
                    modifier = Modifier.size(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(idx.toString(), fontSize = 9.sp, color = Color.Gray)
                }
            }
        }
    }
}

private enum class InputType(val displayName: String, val placeholder: String) {
    HEX("十六进制", "例如: 0xFF 或 FF 或 255"),
    BINARY("二进制", "例如: 11111111 或 0b11111111"),
    DECIMAL("十进制", "例如: 255")
}

private data class ConversionResult(
    val decimal: String,
    val hex: String,
    val binary: String,
    val bits: List<Int>
)

private fun convert(input: String, type: InputType): Result<ConversionResult> {
    val trimmed = input.trim()
    if (trimmed.isEmpty()) {
        return Result.failure(IllegalArgumentException("输入为空"))
    }

    return try {
        val value: Long = when (type) {
            InputType.HEX -> parseHex(trimmed)
            InputType.BINARY -> parseBinary(trimmed)
            InputType.DECIMAL -> trimmed.toLong()
        }

        if (value < 0) {
            return Result.failure(IllegalArgumentException("不支持负数"))
        }

        val hex = "0x${value.toString(16).uppercase()}"
        val binary = value.toString(2)
        val decimal = value.toString()

        // 生成位数组，填充到8的倍数
        val paddedBinary = binary.padStart(((binary.length + 7) / 8) * 8, '0')
        val bits = paddedBinary.map { it.digitToInt() }

        Result.success(ConversionResult(decimal, hex, binary, bits))
    } catch (e: NumberFormatException) {
        Result.failure(IllegalArgumentException("无效的${type.displayName}格式"))
    } catch (e: Exception) {
        Result.failure(IllegalArgumentException("转换错误: ${e.message}"))
    }
}

private fun parseHex(input: String): Long {
    val cleaned = input.lowercase()
        .removePrefix("0x")
        .removePrefix("#")
        .replace(" ", "")
    return cleaned.toLong(16)
}

private fun parseBinary(input: String): Long {
    val cleaned = input
        .removePrefix("0b")
        .removePrefix("0B")
        .replace(" ", "")
    return cleaned.toLong(2)
}
