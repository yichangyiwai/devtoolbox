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
import com.google.gson.*
import org.jetbrains.jewel.ui.component.*
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

@Composable
fun JsonFormatterPanel() {
    var inputText by remember { mutableStateOf("") }
    var outputText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var indentSize by remember { mutableStateOf(2) }
    var parsedJson by remember { mutableStateOf<JsonElement?>(null) }
    var showTreeView by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 工具栏
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DefaultButton(onClick = {
                val result = formatJson(inputText, indentSize)
                outputText = result.first
                errorMessage = result.second
                parsedJson = if (result.second == null) {
                    try { JsonParser.parseString(inputText) } catch (e: Exception) { null }
                } else null
            }) { Text("美化") }

            DefaultButton(onClick = {
                val result = compressJson(inputText)
                outputText = result.first
                errorMessage = result.second
                parsedJson = null
            }) { Text("压缩") }

            DefaultButton(onClick = {
                val result = validateJson(inputText)
                errorMessage = result
                if (result == null) {
                    outputText = "✓ JSON 格式正确"
                }
            }) { Text("校验") }

            DefaultButton(onClick = {
                copyToClipboard(outputText)
            }) { Text("复制结果") }

            Spacer(Modifier.weight(1f))

            Text("缩进:", fontSize = 12.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf(2, 4).forEach { size ->
                    OutlinedButton(
                        onClick = { indentSize = size },
                    ) { Text("$size") }
                }
            }

            Spacer(Modifier.width(8.dp))

            // 树形视图切换
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("视图:", fontSize = 12.sp)
                if (showTreeView) {
                    DefaultButton(onClick = { }) { Text("树形") }
                } else {
                    OutlinedButton(onClick = { showTreeView = true }) { Text("树形") }
                }
                if (!showTreeView) {
                    DefaultButton(onClick = { }) { Text("文本") }
                } else {
                    OutlinedButton(onClick = { showTreeView = false }) { Text("文本") }
                }
            }
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

        // 输入输出区域
        Row(
            modifier = Modifier.fillMaxSize().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 输入区
            Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                Text("输入 JSON:", fontSize = 12.sp, modifier = Modifier.padding(bottom = 4.dp))
                JsonTextArea(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // 输出区
            Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                Text("输出结果:", fontSize = 12.sp, modifier = Modifier.padding(bottom = 4.dp))
                if (showTreeView && parsedJson != null) {
                    JsonTreeView(
                        jsonElement = parsedJson!!,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    JsonTextArea(
                        value = outputText,
                        onValueChange = { outputText = it },
                        modifier = Modifier.fillMaxSize(),
                        readOnly = true
                    )
                }
            }
        }
    }
}

@Composable
private fun JsonTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .border(1.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
            .background(Color(0xFF2B2B2B), RoundedCornerShape(4.dp))
    ) {
        BasicTextField(
            value = value,
            onValueChange = if (readOnly) { _ -> } else onValueChange,
            textStyle = TextStyle(
                color = Color(0xFFA9B7C6),
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace
            ),
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .verticalScroll(scrollState),
            readOnly = readOnly
        )
    }
}

private fun formatJson(input: String, indent: Int): Pair<String, String?> {
    if (input.isBlank()) return "" to "输入为空"
    return try {
        val jsonElement = JsonParser.parseString(input)
        val gson = GsonBuilder().setPrettyPrinting().create()
        val formatted = gson.toJson(jsonElement)
        // 调整缩进
        val result = if (indent == 4) {
            formatted.replace("  ", "    ")
        } else {
            formatted
        }
        result to null
    } catch (e: JsonSyntaxException) {
        "" to "JSON 语法错误: ${e.message}"
    } catch (e: Exception) {
        "" to "解析错误: ${e.message}"
    }
}

private fun compressJson(input: String): Pair<String, String?> {
    if (input.isBlank()) return "" to "输入为空"
    return try {
        val jsonElement = JsonParser.parseString(input)
        val gson = GsonBuilder().create()
        gson.toJson(jsonElement) to null
    } catch (e: JsonSyntaxException) {
        "" to "JSON 语法错误: ${e.message}"
    } catch (e: Exception) {
        "" to "解析错误: ${e.message}"
    }
}

private fun validateJson(input: String): String? {
    if (input.isBlank()) return "输入为空"
    return try {
        JsonParser.parseString(input)
        null
    } catch (e: JsonSyntaxException) {
        "JSON 语法错误: ${e.message}"
    } catch (e: Exception) {
        "解析错误: ${e.message}"
    }
}

private fun copyToClipboard(text: String) {
    val clipboard = Toolkit.getDefaultToolkit().systemClipboard
    clipboard.setContents(StringSelection(text), null)
}

@Composable
private fun JsonTreeView(
    jsonElement: JsonElement,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .border(1.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
            .background(Color(0xFF2B2B2B), RoundedCornerShape(4.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .verticalScroll(scrollState)
        ) {
            JsonNode(element = jsonElement, key = null, depth = 0)
        }
    }
}

@Composable
private fun JsonNode(
    element: JsonElement,
    key: String?,
    depth: Int
) {
    val indent = depth * 16

    when {
        element.isJsonObject -> {
            JsonObjectNode(element.asJsonObject, key, depth, indent)
        }
        element.isJsonArray -> {
            JsonArrayNode(element.asJsonArray, key, depth, indent)
        }
        element.isJsonPrimitive -> {
            JsonPrimitiveNode(element.asJsonPrimitive, key, indent)
        }
        element.isJsonNull -> {
            JsonNullNode(key, indent)
        }
    }
}

@Composable
private fun JsonObjectNode(
    obj: JsonObject,
    key: String?,
    depth: Int,
    indent: Int
) {
    var expanded by remember { mutableStateOf(true) }
    val entries = obj.entrySet().toList()

    Row(
        modifier = Modifier.padding(start = indent.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 折叠按钮
        if (entries.isNotEmpty()) {
            Text(
                text = if (expanded) "▼" else "▶",
                fontSize = 10.sp,
                color = Color.Gray,
                modifier = Modifier
                    .clickable { expanded = !expanded }
                    .padding(end = 4.dp)
            )
        } else {
            Spacer(Modifier.width(14.dp))
        }

        // key 名称
        if (key != null) {
            Text(
                text = "\"$key\"",
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF9876AA)
            )
            Text(
                text = ": ",
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFFA9B7C6)
            )
        }

        // 大括号和摘要
        Text(
            text = "{",
            fontSize = 13.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFA9B7C6)
        )

        if (!expanded && entries.isNotEmpty()) {
            Text(
                text = " ... ${entries.size} 项 ",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = Color.Gray
            )
            Text(
                text = "}",
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFA9B7C6)
            )
        }
    }

    // 展开的子元素
    if (expanded) {
        entries.forEach { (childKey, childElement) ->
            JsonNode(element = childElement, key = childKey, depth = depth + 1)
        }
        Row(modifier = Modifier.padding(start = indent.dp)) {
            Spacer(Modifier.width(14.dp))
            Text(
                text = "}",
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFA9B7C6)
            )
        }
    }
}

@Composable
private fun JsonArrayNode(
    array: JsonArray,
    key: String?,
    depth: Int,
    indent: Int
) {
    var expanded by remember { mutableStateOf(true) }

    Row(
        modifier = Modifier.padding(start = indent.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 折叠按钮
        if (array.size() > 0) {
            Text(
                text = if (expanded) "▼" else "▶",
                fontSize = 10.sp,
                color = Color.Gray,
                modifier = Modifier
                    .clickable { expanded = !expanded }
                    .padding(end = 4.dp)
            )
        } else {
            Spacer(Modifier.width(14.dp))
        }

        // key 名称
        if (key != null) {
            Text(
                text = "\"$key\"",
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF9876AA)
            )
            Text(
                text = ": ",
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFFA9B7C6)
            )
        }

        // 方括号和摘要
        Text(
            text = "[",
            fontSize = 13.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFA9B7C6)
        )

        if (!expanded && array.size() > 0) {
            Text(
                text = " ... ${array.size()} 项 ",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = Color.Gray
            )
            Text(
                text = "]",
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFA9B7C6)
            )
        }
    }

    // 展开的子元素
    if (expanded) {
        array.forEachIndexed { index, childElement ->
            Row(
                modifier = Modifier.padding(start = (indent + 16).dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "[$index]",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color.Gray,
                    modifier = Modifier.padding(end = 4.dp)
                )
            }
            JsonNode(element = childElement, key = null, depth = depth + 1)
        }
        Row(modifier = Modifier.padding(start = indent.dp)) {
            Spacer(Modifier.width(14.dp))
            Text(
                text = "]",
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFA9B7C6)
            )
        }
    }
}

@Composable
private fun JsonPrimitiveNode(
    primitive: JsonPrimitive,
    key: String?,
    indent: Int
) {
    Row(
        modifier = Modifier.padding(start = indent.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(14.dp))

        if (key != null) {
            Text(
                text = "\"$key\"",
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF9876AA)
            )
            Text(
                text = ": ",
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFFA9B7C6)
            )
        }

        val (text, color) = when {
            primitive.isString -> "\"${primitive.asString}\"" to Color(0xFF6A8759)
            primitive.isNumber -> primitive.asNumber.toString() to Color(0xFF6897BB)
            primitive.isBoolean -> primitive.asBoolean.toString() to Color(0xFFCC7832)
            else -> primitive.toString() to Color(0xFFA9B7C6)
        }

        Text(
            text = text,
            fontSize = 13.sp,
            fontFamily = FontFamily.Monospace,
            color = color
        )
    }
}

@Composable
private fun JsonNullNode(
    key: String?,
    indent: Int
) {
    Row(
        modifier = Modifier.padding(start = indent.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(14.dp))

        if (key != null) {
            Text(
                text = "\"$key\"",
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF9876AA)
            )
            Text(
                text = ": ",
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFFA9B7C6)
            )
        }

        Text(
            text = "null",
            fontSize = 13.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFCC7832)
        )
    }
}
