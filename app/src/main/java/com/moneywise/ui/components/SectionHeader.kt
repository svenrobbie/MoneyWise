package com.moneywise.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleMedium,
    showDivider: Boolean = true
) {
    Column(modifier = modifier) {
        if (showDivider) {
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
        }
        Text(
            text = title,
            style = style,
            fontWeight = FontWeight.SemiBold
        )
    }
}
