package com.yichangyiwai.devtoolbox.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yichangyiwai.devtoolbox.domain.byteparser.DataType
import com.yichangyiwai.devtoolbox.domain.byteparser.ParseResult
import com.yichangyiwai.devtoolbox.domain.byteparser.ParseRule
import org.jetbrains.jewel.ui.component.OutlinedButton
import org.jetbrains.jewel.ui.component.Text

@Composable
fun ParseRuleList(
    rules: List<ParseRule>,
    results: List<ParseResult>,
    onUpdateRule: (Int, ParseRule) -> Unit,
    onDeleteRule: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("解析规则:", fontSize = 12.sp)
        rules.forEachIndexed { index, rule ->
            ParseRuleRow(
                index = index,
                rule = rule,
                result = results.getOrNull(index),
                onUpdate = { onUpdateRule(index, it) },
                onDelete = { onDeleteRule(index) }
            )
        }
    }
}

@Composable
private fun ParseRuleRow(
    index: Int,
    rule: ParseRule,
    result: ParseResult?,
    onUpdate: (ParseRule) -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF3C3F41), RoundedCornerShape(4.dp))
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("#${index + 1}", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.width(24.dp))
                Column {
                    Text("偏移", fontSize = 9.sp, color = Color.Gray)
                    SmallTextField(
                        value = rule.offset.toString(),
                        onValueChange = { onUpdate(rule.copy(offset = it.toIntOrNull() ?: 0)) },
                        modifier = Modifier.width(50.dp)
                    )
                }
                Column {
                    Text("长度", fontSize = 9.sp, color = Color.Gray)
                    SmallTextField(
                        value = rule.length.toString(),
                        onValueChange = { onUpdate(rule.copy(length = it.toIntOrNull() ?: 1)) },
                        modifier = Modifier.width(50.dp)
                    )
                }
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
            }
        }

        Box(
            modifier = Modifier
                .width(220.dp)
                .background(
                    if (result?.error != null) Color(0x20E53935) else Color(0x206A8759),
                    RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 8.dp, vertical = 10.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            when {
                result == null -> Text("等待解析", fontSize = 11.sp, color = Color.Gray)
                result.error != null -> Text("❌ ${result.error}", fontSize = 11.sp, color = Color(0xFFE53935))
                else -> result.value?.let {
                    Text(
                        it,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF6A8759)
                    )
                }
            }
        }

        OutlinedButton(onClick = onDelete) {
            Text("×", fontSize = 14.sp)
        }
    }
}

@Composable
private fun SmallTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
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
