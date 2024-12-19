package me.snowcube.tapped.ui.components.widgets

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    color: Color = MaterialTheme.colorScheme.surfaceBright,
    roundedCorner: Boolean = false
) {
    Surface(
        color = backgroundColor,
        shape = if (roundedCorner) RoundedCornerShape(100.dp) else RoundedCornerShape(12.dp),
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = if (roundedCorner) RoundedCornerShape(100.dp) else RoundedCornerShape(14.dp)
            )
            .height(36.dp)
            .padding(2.dp)
    ) {
        Box() {

            var firstSelected = selected == btnList[0]
            val spacerWidthFraction: Float by animateFloatAsState(
                if (firstSelected) 0f else 0.5f,
                label = "spacerWidthFraction"
            )

            Row {
                Spacer(modifier = Modifier.fillMaxWidth(spacerWidthFraction))
                Surface(
                    color = color,
                    shape = if (roundedCorner) RoundedCornerShape(100.dp) else RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .weight(1f)
                ) { }
                Spacer(modifier = Modifier.fillMaxWidth(0.5f - spacerWidthFraction))
            }

            Row {
                btnList.forEach() { btnName ->
                    TextSwitchBtn(
                        selected = selected == btnName,
                        text = btnName,
                        onClick = {
                            onSelectedChanged(btnName)
                        },
                        roundedCorner = roundedCorner,
                        modifier = modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .fillMaxHeight()
                    )
                }
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
    roundedCorner: Boolean
) {
    Button(
        onClick = onClick,
        colors = ButtonColors(
            containerColor = Color.Transparent,
            contentColor = if (selected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
            disabledContainerColor = Color.Red,
            disabledContentColor = Color.White
        ),
        shape = if (roundedCorner) RoundedCornerShape(100.dp) else RoundedCornerShape(12.dp),
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

@Preview
@Composable
private fun TextSwitchRoundedCornerPreview() {
    TappedTheme() {
        TextSwitch(
            selected = "Option 2",
            btnList = listOf(
                "Option 1",
                "Option 2"
            ),
            onSelectedChanged = {},
            roundedCorner = true
        )
    }
}