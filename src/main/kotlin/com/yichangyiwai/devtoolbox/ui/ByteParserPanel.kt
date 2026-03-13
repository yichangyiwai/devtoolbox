package com.yichangyiwai.devtoolbox.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yichangyiwai.devtoolbox.domain.byteparser.ByteParseService
import com.yichangyiwai.devtoolbox.domain.byteparser.ParseResult
import com.yichangyiwai.devtoolbox.domain.byteparser.ParseRule
import com.yichangyiwai.devtoolbox.ui.components.ErrorMessage
import com.yichangyiwai.devtoolbox.ui.components.HexDumpView
import com.yichangyiwai.devtoolbox.ui.components.HexInputField
import com.yichangyiwai.devtoolbox.ui.components.ParseRuleList
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.Text
import java.nio.ByteOrder

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
        Column {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("十六进制数据:", fontSize = 12.sp)
                Spacer(Modifier.weight(1f))
                Text("字节序:", fontSize = 11.sp, color = Color.Gray)
                ByteOrderToggle(byteOrder = byteOrder, onChange = { byteOrder = it })
            }
            Spacer(Modifier.height(4.dp))
            HexInputField(
                value = hexInput,
                onValueChange = { hexInput = it },
                modifier = Modifier.fillMaxWidth().height(80.dp)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DefaultButton(onClick = {
                val result = ByteParseService.parseHexInput(hexInput)
                if (result.isSuccess) {
                    parsedBytes = result.getOrNull()
                    errorMessage = null
                    parseResults = emptyList()
                } else {
                    errorMessage = result.exceptionOrNull()?.message
                    parsedBytes = null
                    parseResults = emptyList()
                }
            }) { Text("解析十六进制") }

            DefaultButton(onClick = {
                parseRules = parseRules + ParseRule()
            }) { Text("+ 添加规则") }

            DefaultButton(onClick = {
                parsedBytes?.let { bytes ->
                    parseResults = ByteParseService.parseAll(bytes, parseRules, byteOrder)
                }
            }) { Text("执行解析") }

            DefaultButton(onClick = {
                parseRules = listOf(ParseRule())
                parseResults = emptyList()
            }) { Text("清空规则") }
        }

        errorMessage?.let { ErrorMessage(it) }

        parsedBytes?.let { bytes ->
            HexDumpView(bytes = bytes, title = "字节预览")
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ParseRuleList(
                rules = parseRules,
                results = parseResults,
                onUpdateRule = { index, rule ->
                    parseRules = parseRules.toMutableList().apply { this[index] = rule }
                    parseResults = emptyList()
                },
                onDeleteRule = { index ->
                    if (parseRules.size > 1) {
                        parseRules = parseRules.toMutableList().apply { removeAt(index) }
                        parseResults = emptyList()
                    }
                }
            )
        }
    }
}

@Composable
private fun ByteOrderToggle(byteOrder: ByteOrder, onChange: (ByteOrder) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ByteOrderButton(
            label = "大端",
            selected = byteOrder == ByteOrder.BIG_ENDIAN,
            onClick = { onChange(ByteOrder.BIG_ENDIAN) }
        )
        ByteOrderButton(
            label = "小端",
            selected = byteOrder == ByteOrder.LITTLE_ENDIAN,
            onClick = { onChange(ByteOrder.LITTLE_ENDIAN) }
        )
    }
}

@Composable
private fun ByteOrderButton(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .height(28.dp)
            .background(
                if (selected) Color(0xFF4A6DA7) else Color.Transparent,
                RoundedCornerShape(4.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            fontSize = 12.sp,
            color = if (selected) Color.White else Color(0xFFA9B7C6)
        )
    }
}
