package com.example.nfc_task.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nfc_task.ui.theme.NFCTaskTheme
import com.example.nfc_task.ui.theme.mediumGrey

@Composable
fun AddTaskComponent(expanded: Boolean) {
    var modifier = Modifier.padding(10.dp)
    if (expanded) {
        modifier = modifier.fillMaxHeight()
    }
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
    ) {
        Column {
            Text("test")
            Text("test")
            Text("test")
            Text("test")
        }
        ControlModule()
    }
}

@Composable
private fun ControlModule() {
    Column(
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Button(
            onClick = {},
            enabled = true,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = Color(0x0A000000),
                disabledContentColor = mediumGrey
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(
                "写入",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
        ) {
            CancelButton(
                modifier = Modifier.weight(1f),
                enabled = true
            ) { }
            ConfirmButton(
                modifier = Modifier.weight(1f),
                enabled = false
            ) { }
        }
    }
}

@Composable
private fun ConfirmButton(
    modifier: Modifier = Modifier,
    text: String = "确认",
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        disabledContainerColor = Color(0x5A000000),
        disabledContentColor = Color.White
    ),
    onClick: () -> Unit,
) {
    OperationButton(
        modifier = modifier,
        text = text,
        enabled = enabled,
        colors = colors,
        onClick = onClick
    )
}

@Composable
private fun CancelButton(
    modifier: Modifier = Modifier,
    text: String = "取消",
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = Color(0x1A000000),
        contentColor = Color.Black,
        disabledContainerColor = Color(0x5A000000),
        disabledContentColor = Color.White
    ),
    onClick: () -> Unit,
) {
    OperationButton(
        modifier = modifier,
        text = text,
        enabled = enabled,
        colors = colors,
        onClick = onClick
    )
}

@Composable
private fun OperationButton(
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        disabledContainerColor = Color(0x0A000000),
        disabledContentColor = mediumGrey
    ),
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = colors,
        modifier = modifier
            .height(48.dp)
    ) {
        Text(
            text,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview
@Composable
fun AddTaskComponentPreview() {
    NFCTaskTheme {
        Surface(
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            AddTaskComponent(false)
        }
    }
}