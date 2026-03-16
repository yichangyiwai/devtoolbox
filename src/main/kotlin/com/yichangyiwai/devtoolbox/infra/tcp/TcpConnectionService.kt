package com.yichangyiwai.devtoolbox.infra.tcp

import com.yichangyiwai.devtoolbox.domain.tcp.TcpReceivedMessage
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Collections
import kotlin.concurrent.thread

class TcpClientService {
    private var socket: Socket? = null
    private var output: BufferedOutputStream? = null

    @Synchronized
    fun connect(host: String, port: Int) {
        disconnect()
        socket = Socket(host, port)
        output = BufferedOutputStream(socket!!.getOutputStream())
    }

    @Synchronized
    fun send(data: ByteArray) {
        val currentOutput = output ?: throw IllegalStateException("尚未连接")
        currentOutput.write(data)
        currentOutput.flush()
    }

    @Synchronized
    fun disconnect() {
        output?.close()
        socket?.close()
        output = null
        socket = null
    }

    fun isConnected(): Boolean = socket?.isConnected == true && socket?.isClosed == false
}

class TcpReceiverClientService {
    @Volatile
    private var running = false
    private var socket: Socket? = null
    private var output: BufferedOutputStream? = null

    @Synchronized
    fun start(
        host: String,
        port: Int,
        onMessage: (TcpReceivedMessage) -> Unit,
        onError: (String) -> Unit,
        onDisconnected: () -> Unit,
    ) {
        if (running) throw IllegalStateException("连接已启动")
        val connected = Socket(host, port)
        socket = connected
        output = BufferedOutputStream(connected.getOutputStream())
        running = true
        thread(isDaemon = true, name = "devtoolbox-tcp-receiver-client") {
            try {
                val input = BufferedInputStream(connected.getInputStream())
                val buffer = ByteArray(4096)
                while (running && !connected.isClosed) {
                    val size = input.read(buffer)
                    if (size <= 0) break
                    onMessage(
                        TcpReceivedMessage(
                            timestamp = tcpTimestamp(),
                            remoteAddress = "${connected.inetAddress.hostAddress}:${connected.port}",
                            data = buffer.copyOf(size)
                        )
                    )
                }
            } catch (e: Exception) {
                if (running) onError(e.message ?: "TCP 接收失败")
            } finally {
                val shouldNotifyDisconnected = running
                running = false
                closeResources()
                if (shouldNotifyDisconnected) {
                    onDisconnected()
                }
            }
        }
    }

    @Synchronized
    fun send(data: ByteArray) {
        if (!running) throw IllegalStateException("连接未建立")
        val currentOutput = output ?: throw IllegalStateException("连接未建立")
        currentOutput.write(data)
        currentOutput.flush()
    }

    @Synchronized
    fun stop() {
        running = false
        closeResources()
    }

    fun isRunning(): Boolean = running && socket?.isClosed == false

    @Synchronized
    private fun closeResources() {
        output?.close()
        socket?.close()
        output = null
        socket = null
    }
}

class TcpServerService {
    private var serverSocket: ServerSocket? = null
    private var running = false
    private val clients = Collections.synchronizedList(mutableListOf<Socket>())

    fun start(host: String, port: Int, onMessage: (TcpReceivedMessage) -> Unit, onError: (String) -> Unit) {
        if (running) throw IllegalStateException("监听已启动")
        running = true
        thread(isDaemon = true, name = "devtoolbox-tcp-server") {
            try {
                val socket = ServerSocket(port, 50, InetAddress.getByName(host))
                serverSocket = socket
                while (running) {
                    val client = socket.accept()
                    clients.add(client)
                    thread(isDaemon = true, name = "devtoolbox-tcp-client-reader") {
                        client.use { accepted ->
                            try {
                                val input = BufferedInputStream(accepted.getInputStream())
                                val buffer = ByteArray(4096)
                                while (running && !accepted.isClosed) {
                                    val size = input.read(buffer)
                                    if (size <= 0) break
                                    onMessage(
                                        TcpReceivedMessage(
                                            timestamp = tcpTimestamp(),
                                            remoteAddress = accepted.inetAddress.hostAddress,
                                            data = buffer.copyOf(size)
                                        )
                                    )
                                }
                            } finally {
                                clients.remove(accepted)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                if (running) onError(e.message ?: "TCP 监听失败")
            } finally {
                running = false
                try {
                    serverSocket?.close()
                } catch (_: Exception) {
                }
                serverSocket = null
            }
        }
    }

    @Synchronized
    fun sendToClients(data: ByteArray) {
        if (!running) throw IllegalStateException("监听未启动")
        val disconnectedClients = mutableListOf<Socket>()
        clients.forEach { client ->
            try {
                BufferedOutputStream(client.getOutputStream()).apply {
                    write(data)
                    flush()
                }
            } catch (_: Exception) {
                disconnectedClients.add(client)
            }
        }
        clients.removeAll(disconnectedClients)
        if (clients.isEmpty()) {
            throw IllegalStateException("当前没有可发送 Ping 的客户端连接")
        }
    }

    fun stop() {
        running = false
        clients.toList().forEach {
            try {
                it.close()
            } catch (_: Exception) {
            }
        }
        clients.clear()
        serverSocket?.close()
        serverSocket = null
    }

    fun isRunning(): Boolean = running
}

private fun tcpTimestamp(): String = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
