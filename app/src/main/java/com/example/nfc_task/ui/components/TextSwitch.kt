package com.example.nfc_task.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.nfc_task.ui.theme.darkGrey

@Composable
fun TextSwitch(
    selected: String,
    btnList: List<String>,
    onSelectedChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0x0C000000),
        shape = MaterialTheme.shapes.small,
        modifier = modifier
            .height(40.dp)
    ) {
        Row(
            modifier = modifier
                .padding(3.dp)
        ) {
            btnList.forEach() { btnName ->
                TextSwitchBtn(
                    selected = selected == btnName,
                    text = btnName,
                    onClick = {
                        onSelectedChanged(btnName)
                    },
                    modifier = modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun TextSwitchBtn(
    selected: Boolean,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonColors(
            containerColor = if (selected) MaterialTheme.colorScheme.secondary else Color(0x00000000),
            contentColor = if (selected) MaterialTheme.colorScheme.onSecondary else darkGrey,
            disabledContainerColor = Color(0x00000000),
            disabledContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = MaterialTheme.shapes.small,
        contentPadding = PaddingValues(0.dp),
        modifier = modifier
    ) {
        Text(text, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}