package com.yichangyiwai.devtoolbox.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yichangyiwai.devtoolbox.domain.hex.HexCodec
import org.jetbrains.jewel.ui.component.Text
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun HexDumpView(
    bytes: ByteArray,
    title: String,
    modifier: Modifier = Modifier,
    bytesPerRow: Int = 8,
) {
    val scrollState = rememberScrollState()
    val rowCount = (bytes.size + bytesPerRow - 1) / bytesPerRow

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF3C3F41), RoundedCornerShape(4.dp))
            .padding(8.dp)
    ) {
        Text("$title (共 ${bytes.size} 字节, ${rowCount} 行):", fontSize = 11.sp, color = Color.Gray)
        Spacer(Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("行", fontSize = 10.sp, color = Color.Gray, modifier = Modifier.width(30.dp))
            Text("偏移", fontSize = 10.sp, color = Color.Gray, modifier = Modifier.width(52.dp))
            Text("十六进制", fontSize = 10.sp, color = Color.Gray, modifier = Modifier.width(220.dp))
            Text("ASCII", fontSize = 10.sp, color = Color.Gray)
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 220.dp)
                .verticalScroll(scrollState)
        ) {
            bytes.toList().chunked(bytesPerRow).forEachIndexed { rowIndex, rowBytes ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                    Text(
                        "${rowIndex + 1}",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color.Gray,
                        modifier = Modifier.width(30.dp)
                    )
                    Text(
                        "%04X".format(rowIndex * bytesPerRow),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF6897BB),
                        modifier = Modifier.width(52.dp)
                    )
                    Text(
                        HexCodec.formatHex(rowBytes.map { it.toByte() }.toByteArray()),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF6A8759),
                        modifier = Modifier.width(220.dp)
                    )
                    Text(
                        rowBytes.map {
                            val value = it.toInt() and 0xFF
                            if (value in 32..126) value.toChar() else '.'
                        }.joinToString(""),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFFCC7832)
                    )
                }
            }
        }
    }
}
