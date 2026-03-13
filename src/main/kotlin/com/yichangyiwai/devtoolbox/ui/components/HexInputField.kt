package com.yichangyiwai.devtoolbox.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.jewel.ui.component.Text
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun HexInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "输入十六进制数据，如: 48 65 6C 6C 6F 或 0x48656C6C6F",
    readOnly: Boolean = false,
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
            onValueChange = if (readOnly) ({ _: String -> }) else onValueChange,
            textStyle = TextStyle(
                color = Color(0xFFA9B7C6),
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace
            ),
            modifier = Modifier.fillMaxSize(),
            readOnly = readOnly
        )
    }
}
