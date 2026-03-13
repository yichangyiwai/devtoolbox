package com.yichangyiwai.devtoolbox.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.jewel.ui.component.Text
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun ErrorMessage(message: String) {
    Text(
        text = "❌ $message",
        color = Color(0xFFE53935),
        fontSize = 12.sp,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x20E53935), RoundedCornerShape(4.dp))
            .padding(8.dp)
    )
}

@Composable
fun SuccessMessage(message: String) {
    Text(
        text = "✓ $message",
        color = Color(0xFF6A8759),
        fontSize = 12.sp,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x206A8759), RoundedCornerShape(4.dp))
            .padding(8.dp)
    )
}
