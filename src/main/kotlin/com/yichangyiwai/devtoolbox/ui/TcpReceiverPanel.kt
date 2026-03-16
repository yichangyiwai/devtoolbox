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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yichangyiwai.devtoolbox.domain.byteparser.ByteParseService
import com.yichangyiwai.devtoolbox.domain.byteparser.ParseResult
import com.yichangyiwai.devtoolbox.domain.byteparser.ParseRule
import com.yichangyiwai.devtoolbox.domain.hex.HexCodec
import com.yichangyiwai.devtoolbox.domain.tcp.TcpReceiveMode
import com.yichangyiwai.devtoolbox.domain.tcp.TcpReceivedMessage
import com.yichangyiwai.devtoolbox.infra.tcp.TcpReceiverClientService
import com.yichangyiwai.devtoolbox.infra.tcp.TcpServerService
import com.yichangyiwai.devtoolbox.ui.components.ErrorMessage
import com.yichangyiwai.devtoolbox.ui.components.ParseRuleList
import com.yichangyiwai.devtoolbox.ui.components.SuccessMessage
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.OutlinedButton
import org.jetbrains.jewel.ui.component.Text
import java.nio.ByteOrder

private val addRuleButtonWidth = 140.dp

@Composable
fun TcpReceiverPanel() {
    val serverService = remember { TcpServerService() }
    val clientService = remember { TcpReceiverClientService() }
    var receiveMode by remember { mutableStateOf(TcpReceiveMode.SERVER) }
    var serverHost by remember { mutableStateOf("0.0.0.0") }
    var serverPort by remember { mutableStateOf("9000") }
    var clientHost by remember { mutableStateOf("127.0.0.1") }
    var clientPort by remember { mutableStateOf("9000") }
    var active by remember { mutableStateOf(false) }
    var pingHexInput by remember { mutableStateOf("50 49 4E 47") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var messages by remember { mutableStateOf(listOf<TcpReceivedMessage>()) }
    var selectedIndex by remember { mutableStateOf(0) }
    var parseRules by remember { mutableStateOf(listOf(ParseRule())) }
    var parseResults by remember { mutableStateOf<List<ParseResult>>(emptyList()) }
    var byteOrder by remember { mutableStateOf(ByteOrder.BIG_ENDIAN) }

    DisposableEffect(Unit) {
        onDispose {
            serverService.stop()
            clientService.stop()
        }
    }

    fun refreshResults() {
        val bytes = messages.getOrNull(selectedIndex)?.data
        parseResults = if (bytes == null || parseRules.isEmpty()) {
            emptyList()
        } else {
            ByteParseService.parseAll(bytes, parseRules, byteOrder)
        }
    }

    fun createNextParseRule(): ParseRule {
        val previousRule = parseRules.lastOrNull() ?: return ParseRule()
        return ParseRule(
            offset = previousRule.offset.coerceAtLeast(0) + previousRule.length.coerceAtLeast(1)
        )
    }

    fun handleIncomingMessage(message: TcpReceivedMessage) {
        messages = listOf(message) + messages
        selectedIndex = 0
        parseResults = ByteParseService.parseAll(message.data, parseRules, byteOrder)
        successMessage = "收到 ${message.data.size} 字节，来自 ${message.remoteAddress}"
        errorMessage = null
    }

    fun stopMode(mode: TcpReceiveMode, statusMessage: String? = null) {
        when (mode) {
            TcpReceiveMode.SERVER -> serverService.stop()
            TcpReceiveMode.CLIENT -> clientService.stop()
        }
        active = false
        if (statusMessage != null) {
            successMessage = statusMessage
            errorMessage = null
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ReceiveModeSelector(receiveMode = receiveMode, onChange = { mode ->
            if (mode != receiveMode) {
                if (active) {
                    stopMode(receiveMode)
                }
                receiveMode = mode
                errorMessage = null
                successMessage = "已切换为${mode.displayName}"
            }
        })

        TcpReceiverBar(
            receiveMode = receiveMode,
            host = if (receiveMode == TcpReceiveMode.SERVER) serverHost else clientHost,
            port = if (receiveMode == TcpReceiveMode.SERVER) serverPort else clientPort,
            active = active,
            pingHexInput = pingHexInput,
            onHostChange = {
                if (receiveMode == TcpReceiveMode.SERVER) {
                    serverHost = it
                } else {
                    clientHost = it
                }
            },
            onPortChange = {
                if (receiveMode == TcpReceiveMode.SERVER) {
                    serverPort = it
                } else {
                    clientPort = it
                }
            },
            onPingHexChange = { pingHexInput = it },
            onStart = {
                val currentHost = if (receiveMode == TcpReceiveMode.SERVER) serverHost else clientHost
                val currentPort = if (receiveMode == TcpReceiveMode.SERVER) serverPort else clientPort
                val validatedPort = currentPort.toIntOrNull()
                if (currentHost.isBlank() || validatedPort == null || validatedPort !in 1..65535) {
                    errorMessage = if (receiveMode == TcpReceiveMode.SERVER) {
                        "请输入合法的监听主机和端口"
                    } else {
                        "请输入合法的目标服务端主机和端口"
                    }
                    successMessage = null
                } else {
                    try {
                        when (receiveMode) {
                            TcpReceiveMode.SERVER -> serverService.start(
                                host = currentHost,
                                port = validatedPort,
                                onMessage = { message -> handleIncomingMessage(message) },
                                onError = {
                                    errorMessage = it
                                    successMessage = null
                                    active = false
                                }
                            )

                            TcpReceiveMode.CLIENT -> clientService.start(
                                host = currentHost,
                                port = validatedPort,
                                onMessage = { message -> handleIncomingMessage(message) },
                                onError = {
                                    errorMessage = it
                                    successMessage = null
                                    active = false
                                },
                                onDisconnected = {
                                    active = false
                                    if (errorMessage == null) {
                                        successMessage = "与服务端连接已断开"
                                    }
                                }
                            )
                        }
                        active = true
                        errorMessage = null
                        successMessage = if (receiveMode == TcpReceiveMode.SERVER) {
                            "开始监听 $currentHost:$validatedPort"
                        } else {
                            "已连接到 $currentHost:$validatedPort"
                        }
                    } catch (e: Exception) {
                        active = false
                        errorMessage = e.message ?: if (receiveMode == TcpReceiveMode.SERVER) "监听失败" else "连接失败"
                        successMessage = null
                    }
                }
            },
            onStop = {
                stopMode(
                    mode = receiveMode,
                    statusMessage = if (receiveMode == TcpReceiveMode.SERVER) "已停止监听" else "已断开与服务端的连接"
                )
            },
            onPing = {
                val pingResult = HexCodec.parseHexString(pingHexInput)
                if (pingResult.isSuccess) {
                    try {
                        val pingBytes = pingResult.getOrThrow()
                        when (receiveMode) {
                            TcpReceiveMode.SERVER -> serverService.sendToClients(pingBytes)
                            TcpReceiveMode.CLIENT -> clientService.send(pingBytes)
                        }
                        successMessage = if (receiveMode == TcpReceiveMode.SERVER) {
                            "Ping 已发送到已连接客户端，共 ${pingBytes.size} 字节"
                        } else {
                            "Ping 已发送到目标服务端，共 ${pingBytes.size} 字节"
                        }
                        errorMessage = null
                    } catch (e: Exception) {
                        errorMessage = e.message ?: "Ping 发送失败"
                        successMessage = null
                    }
                } else {
                    errorMessage = pingResult.exceptionOrNull()?.message
                    successMessage = null
                }
            }
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("字节序", fontSize = 12.sp)
            ByteOrderChoice(label = "大端", selected = byteOrder == ByteOrder.BIG_ENDIAN) {
                byteOrder = ByteOrder.BIG_ENDIAN
                refreshResults()
            }
            ByteOrderChoice(label = "小端", selected = byteOrder == ByteOrder.LITTLE_ENDIAN) {
                byteOrder = ByteOrder.LITTLE_ENDIAN
                refreshResults()
            }
        }

        errorMessage?.let { ErrorMessage(it) }
        successMessage?.let { SuccessMessage(it) }

        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            MessageList(
                messages = messages,
                selectedIndex = selectedIndex,
                onSelect = {
                    selectedIndex = it
                    refreshResults()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )

            if (parseRules.isEmpty()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color(0xFF3C3F41), RoundedCornerShape(4.dp))
                        .border(1.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "暂无解析规则，请点击下方按钮开始配置。",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        DefaultButton(modifier = Modifier.width(addRuleButtonWidth), onClick = {
                            parseRules = listOf(ParseRule())
                            errorMessage = null
                            successMessage = null
                            refreshResults()
                        }) {
                            Text("+ 添加规则")
                        }
                    }
                }
            } else {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ParseRuleList(
                        rules = parseRules,
                        results = parseResults,
                        onUpdateRule = { index, rule ->
                            parseRules = parseRules.toMutableList().apply { this[index] = rule }
                            refreshResults()
                        },
                        onDeleteRule = { index ->
                            parseRules = parseRules.toMutableList().apply { removeAt(index) }
                            refreshResults()
                            if (parseRules.isEmpty()) {
                                errorMessage = null
                                successMessage = null
                            }
                        },
                        modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        DefaultButton(modifier = Modifier.width(addRuleButtonWidth), onClick = {
                            parseRules = parseRules + createNextParseRule()
                            errorMessage = null
                            successMessage = null
                            refreshResults()
                        }) {
                            Text("+ 添加规则")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReceiveModeSelector(receiveMode: TcpReceiveMode, onChange: (TcpReceiveMode) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Text("接收模式", fontSize = 12.sp)
        TcpReceiveMode.entries.forEach { mode ->
            if (receiveMode == mode) {
                DefaultButton(onClick = {}) { Text(mode.displayName) }
            } else {
                OutlinedButton(onClick = { onChange(mode) }) { Text(mode.displayName) }
            }
        }
    }
}

@Composable
private fun TcpReceiverBar(
    receiveMode: TcpReceiveMode,
    host: String,
    port: String,
    active: Boolean,
    pingHexInput: String,
    onHostChange: (String) -> Unit,
    onPortChange: (String) -> Unit,
    onPingHexChange: (String) -> Unit,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onPing: () -> Unit,
) {
    val hostLabel = if (receiveMode == TcpReceiveMode.SERVER) "监听主机" else "目标主机"
    val portLabel = if (receiveMode == TcpReceiveMode.SERVER) "监听端口" else "目标端口"
    val primaryButtonText = when (receiveMode) {
        TcpReceiveMode.SERVER -> if (active) "停止监听" else "开始监听"
        TcpReceiveMode.CLIENT -> if (active) "断开" else "连接"
    }
    val statusText = when (receiveMode) {
        TcpReceiveMode.SERVER -> if (active) "状态: 监听中" else "状态: 未监听"
        TcpReceiveMode.CLIENT -> if (active) "状态: 已连接" else "状态: 未连接"
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(hostLabel, fontSize = 12.sp)
        ReceiverInlineField(value = host, onValueChange = onHostChange, modifier = Modifier.width(150.dp))
        Text(portLabel, fontSize = 12.sp)
        ReceiverInlineField(value = port, onValueChange = onPortChange, modifier = Modifier.width(90.dp))
        if (active) {
            DefaultButton(onClick = onStop) { Text(primaryButtonText) }
        } else {
            DefaultButton(onClick = onStart) { Text(primaryButtonText) }
        }
        Text("Ping", fontSize = 12.sp)
        ReceiverInlineField(value = pingHexInput, onValueChange = onPingHexChange, modifier = Modifier.width(160.dp))
        DefaultButton(onClick = onPing) { Text("Ping") }
        Text(statusText, fontSize = 11.sp, color = if (active) Color(0xFF6A8759) else Color.Gray)
    }
}

@Composable
private fun MessageList(
    messages: List<TcpReceivedMessage>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF3C3F41), RoundedCornerShape(4.dp))
            .padding(8.dp)
    ) {
        Text("接收记录", fontSize = 11.sp, color = Color.Gray)
        Spacer(Modifier.height(4.dp))
        Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            messages.forEachIndexed { index, message ->
                val selected = index == selectedIndex
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (selected) Color(0x204A6DA7) else Color(0xFF2B2B2B), RoundedCornerShape(4.dp))
                        .border(1.dp, if (selected) Color(0xFF6A9FD9) else Color.Transparent, RoundedCornerShape(4.dp))
                        .clickable { onSelect(index) }
                        .padding(8.dp)
                ) {
                    Text("[${message.timestamp}] ${message.remoteAddress} / ${message.data.size} 字节", fontSize = 11.sp, color = Color(0xFFA9B7C6))
                    Text(HexCodec.formatHex(message.data), fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = Color(0xFF6A8759))
                }
            }
        }
    }
}

@Composable
private fun ReceiverInlineField(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
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
            cursorBrush = SolidColor(Color.White),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ByteOrderChoice(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(if (selected) Color(0xFF4A6DA7) else Color.Transparent, RoundedCornerShape(4.dp))
            .border(1.dp, if (selected) Color(0xFF6A9FD9) else Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Text(label, fontSize = 11.sp, color = if (selected) Color.White else Color(0xFFA9B7C6))
    }
}
