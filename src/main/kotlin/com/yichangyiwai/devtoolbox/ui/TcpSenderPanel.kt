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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yichangyiwai.devtoolbox.domain.byteparser.DataType
import com.yichangyiwai.devtoolbox.domain.hex.HexCodec
import com.yichangyiwai.devtoolbox.domain.tcp.TcpMessageBuilder
import com.yichangyiwai.devtoolbox.domain.tcp.TcpMessageField
import com.yichangyiwai.devtoolbox.domain.tcp.TcpSendMode
import com.yichangyiwai.devtoolbox.domain.tcp.ValueInputMode
import com.yichangyiwai.devtoolbox.infra.tcp.TcpClientService
import com.yichangyiwai.devtoolbox.ui.components.ErrorMessage
import com.yichangyiwai.devtoolbox.ui.components.HexDumpView
import com.yichangyiwai.devtoolbox.ui.components.HexInputField
import com.yichangyiwai.devtoolbox.ui.components.SuccessMessage
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.OutlinedButton
import org.jetbrains.jewel.ui.component.Text
import java.nio.ByteOrder
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun TcpSenderPanel() {
    val clientService = remember { TcpClientService() }
    var host by remember { mutableStateOf("127.0.0.1") }
    var port by remember { mutableStateOf("9000") }
    var sendMode by remember { mutableStateOf(TcpSendMode.RULES) }
    var hexInput by remember { mutableStateOf("") }
    var fields by remember { mutableStateOf(listOf(TcpMessageField())) }
    var previewBytes by remember { mutableStateOf<ByteArray?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var logs by remember { mutableStateOf(listOf<String>()) }
    var connected by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose { clientService.disconnect() }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TcpConnectionBar(
            host = host,
            port = port,
            connected = connected,
            onHostChange = { host = it },
            onPortChange = { port = it },
            onConnect = {
                val validatedPort = port.toIntOrNull()
                if (host.isBlank() || validatedPort == null || validatedPort !in 1..65535) {
                    errorMessage = "请输入合法的主机和端口"
                    successMessage = null
                } else {
                    try {
                        clientService.connect(host, validatedPort)
                        connected = true
                        errorMessage = null
                        successMessage = "已连接到 $host:$validatedPort"
                    } catch (e: Exception) {
                        connected = false
                        errorMessage = e.message ?: "连接失败"
                        successMessage = null
                    }
                }
            },
            onDisconnect = {
                clientService.disconnect()
                connected = false
                successMessage = "连接已断开"
                errorMessage = null
            }
        )

        SendModeSelector(sendMode = sendMode, onChange = {
            sendMode = it
            previewBytes = null
            errorMessage = null
            successMessage = null
        })

        if (sendMode == TcpSendMode.HEX) {
            HexInputField(
                value = hexInput,
                onValueChange = { hexInput = it },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                placeholder = "输入完整十六进制报文"
            )
        } else {
            RuleFieldList(
                fields = fields,
                onUpdate = { index, field ->
                    fields = fields.toMutableList().apply { this[index] = field }
                    previewBytes = null
                },
                onDelete = { index ->
                    if (fields.size > 1) {
                        fields = fields.toMutableList().apply { removeAt(index) }
                        previewBytes = null
                    }
                }
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DefaultButton(onClick = { fields = fields + TcpMessageField(offset = fields.maxOf { it.offset + it.length }) }) {
                    Text("+ 添加字段")
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DefaultButton(onClick = {
                val result = if (sendMode == TcpSendMode.HEX) {
                    HexCodec.parseHexString(hexInput)
                } else {
                    TcpMessageBuilder.build(fields)
                }
                if (result.isSuccess) {
                    previewBytes = result.getOrNull()
                    errorMessage = null
                    successMessage = "报文组装成功"
                } else {
                    previewBytes = null
                    errorMessage = result.exceptionOrNull()?.message
                    successMessage = null
                }
            }) { Text("预览报文") }

            DefaultButton(onClick = {
                if (!connected || !clientService.isConnected()) {
                    errorMessage = "请先连接 TCP 服务端"
                    successMessage = null
                    return@DefaultButton
                }
                val result = if (sendMode == TcpSendMode.HEX) {
                    HexCodec.parseHexString(hexInput)
                } else {
                    TcpMessageBuilder.build(fields)
                }
                if (result.isSuccess) {
                    val bytes = result.getOrThrow()
                    previewBytes = bytes
                    try {
                        clientService.send(bytes)
                        val hex = HexCodec.formatHex(bytes)
                        logs = listOf("[${timestamp()}] 实际发送: $hex") + logs
                        errorMessage = null
                        successMessage = "发送成功，共 ${bytes.size} 字节"
                    } catch (e: Exception) {
                        errorMessage = e.message ?: "发送失败"
                        successMessage = null
                    }
                } else {
                    errorMessage = result.exceptionOrNull()?.message
                    successMessage = null
                }
            }) { Text("发送") }
        }

        errorMessage?.let { ErrorMessage(it) }
        successMessage?.let { SuccessMessage(it) }

        previewBytes?.let {
            HexDumpView(bytes = it, title = "发送预览")
        }

        LogPanel(title = "发送日志", logs = logs, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun TcpConnectionBar(
    host: String,
    port: String,
    connected: Boolean,
    onHostChange: (String) -> Unit,
    onPortChange: (String) -> Unit,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("主机", fontSize = 12.sp)
        InlineTextField(value = host, onValueChange = onHostChange, modifier = Modifier.width(140.dp))
        Text("端口", fontSize = 12.sp)
        InlineTextField(value = port, onValueChange = onPortChange, modifier = Modifier.width(90.dp))
        if (connected) {
            DefaultButton(onClick = onDisconnect) { Text("断开") }
        } else {
            DefaultButton(onClick = onConnect) { Text("连接") }
        }
        Text(if (connected) "状态: 已连接" else "状态: 未连接", fontSize = 11.sp, color = if (connected) Color(0xFF6A8759) else Color.Gray)
    }
}

@Composable
private fun SendModeSelector(sendMode: TcpSendMode, onChange: (TcpSendMode) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Text("发送模式", fontSize = 12.sp)
        TcpSendMode.entries.forEach { mode ->
            val selected = sendMode == mode
            if (selected) {
                DefaultButton(onClick = {}) { Text(mode.displayName) }
            } else {
                OutlinedButton(onClick = { onChange(mode) }) { Text(mode.displayName) }
            }
        }
    }
}

@Composable
private fun RuleFieldList(
    fields: List<TcpMessageField>,
    onUpdate: (Int, TcpMessageField) -> Unit,
    onDelete: (Int) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        fields.forEachIndexed { index, field ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF3C3F41), RoundedCornerShape(4.dp))
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("#${index + 1}", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.width(24.dp))
                    LabeledInlineField("偏移", field.offset.toString(), { onUpdate(index, field.copy(offset = it.toIntOrNull() ?: 0)) }, 60.dp)
                    LabeledInlineField("长度", field.length.toString(), { onUpdate(index, field.copy(length = it.toIntOrNull() ?: 1)) }, 60.dp)
                    Spacer(Modifier.weight(1f))
                    OutlinedButton(onClick = { onDelete(index) }) { Text("×") }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("类型", fontSize = 10.sp, color = Color.Gray)
                    DataType.entries.forEach { type ->
                        val selected = field.type == type
                        Box(
                            modifier = Modifier
                                .background(if (selected) Color(0xFF4A6DA7) else Color.Transparent, RoundedCornerShape(4.dp))
                                .border(1.dp, if (selected) Color(0xFF6A9FD9) else Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                .clickable { onUpdate(index, field.copy(type = type)) }
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                        ) {
                            Text(type.displayName, fontSize = 10.sp, color = if (selected) Color.White else Color(0xFFA9B7C6))
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("输入", fontSize = 10.sp, color = Color.Gray)
                    ValueInputMode.entries.forEach { mode ->
                        val selected = field.inputMode == mode
                        Box(
                            modifier = Modifier
                                .background(if (selected) Color(0xFF4A6DA7) else Color.Transparent, RoundedCornerShape(4.dp))
                                .border(1.dp, if (selected) Color(0xFF6A9FD9) else Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                .clickable { onUpdate(index, field.copy(inputMode = mode)) }
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                        ) {
                            Text(mode.displayName, fontSize = 10.sp, color = if (selected) Color.White else Color(0xFFA9B7C6))
                        }
                    }
                    Text("字节序", fontSize = 10.sp, color = Color.Gray)
                    listOf(ByteOrder.BIG_ENDIAN to "大端", ByteOrder.LITTLE_ENDIAN to "小端").forEach { (order, label) ->
                        val selected = field.byteOrder == order
                        Box(
                            modifier = Modifier
                                .background(if (selected) Color(0xFF4A6DA7) else Color.Transparent, RoundedCornerShape(4.dp))
                                .border(1.dp, if (selected) Color(0xFF6A9FD9) else Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                .clickable { onUpdate(index, field.copy(byteOrder = order)) }
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                        ) {
                            Text(label, fontSize = 10.sp, color = if (selected) Color.White else Color(0xFFA9B7C6))
                        }
                    }
                }
                Column {
                    Text(if (field.inputMode == ValueInputMode.HEX) "Hex 内容" else "数值/字符串", fontSize = 10.sp, color = Color.Gray)
                    InlineTextField(
                        value = field.inputValue,
                        onValueChange = { onUpdate(index, field.copy(inputValue = it)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun LabeledInlineField(label: String, value: String, onValueChange: (String) -> Unit, width: androidx.compose.ui.unit.Dp) {
    Column {
        Text(label, fontSize = 9.sp, color = Color.Gray)
        InlineTextField(value = value, onValueChange = onValueChange, modifier = Modifier.width(width))
    }
}

@Composable
private fun InlineTextField(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(28.dp)
            .border(1.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
            .background(Color(0xFF2B2B2B), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(color = Color(0xFFA9B7C6), fontSize = 12.sp, fontFamily = FontFamily.Monospace),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun LogPanel(title: String, logs: List<String>, modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF3C3F41), RoundedCornerShape(4.dp))
            .padding(8.dp)
    ) {
        Text(title, fontSize = 11.sp, color = Color.Gray)
        Spacer(Modifier.height(4.dp))
        Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            logs.forEach { log ->
                Text(log, fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = Color(0xFFA9B7C6))
            }
        }
    }
}

private fun timestamp(): String = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
