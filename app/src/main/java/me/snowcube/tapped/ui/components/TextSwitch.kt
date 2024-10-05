package me.snowcube.tapped.ui.components

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.snowcube.tapped.ui.theme.TappedTheme

@Composable
fun TextSwitch(
    selected: String,
    btnList: List<String>,
    onSelectedChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceDim,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    Surface(
        color = backgroundColor,
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
                    color = color,
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
    modifier: Modifier,
    color: Color
) {
    Button(
        onClick = onClick,
        colors = ButtonColors(
            containerColor = if (selected) color else Color.Transparent,
            contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            disabledContainerColor = Color.Red,
            disabledContentColor = Color.White
        ),
        shape = MaterialTheme.shapes.small,
        contentPadding = PaddingValues(0.dp),
        modifier = modifier
    ) {
        Text(text, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

@Preview
@Composable
private fun TextSwitchPreview() {
    TappedTheme() {
        TextSwitch(
            selected = "Option 1",
            btnList = listOf(
                "Option 1",
                "Option 2"
            ),
            onSelectedChanged = {}
        )
    }
}