package com.example.nfc_task.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nfc_task.ui.theme.NFCTaskTheme

@Composable
fun BottomBar(
    screenItems: List<HomeScreen>,
    currentScreenRoute: String,
    onItemClick: (targetRoute: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween, modifier = modifier
//                .padding(
//                    horizontal = 10.dp, vertical = 5.dp
//                )
                .padding(
                    top = 5.dp,
                    start = 10.dp,
                    end = 10.dp,
                    bottom = 25.dp
                )
                .fillMaxWidth()
        ) {
            Surface(
                shape = MaterialTheme.shapes.extraLarge, modifier = modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.extraLarge
                    )
                    .height(60.dp)
                    .width(230.dp)
                    .padding(4.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = modifier.background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.extraLarge
                    )
                ) {
                    screenItems.forEach { screen ->
                        NavBtn(
                            text = stringResource(screen.resourceId),
                            icon = screen.icon,
                            selected = currentScreenRoute == screen.route
                        ) {
                            onItemClick(screen.route)
                        }
                    }
                }
            }

            FloatingActionButton(
                shape = MaterialTheme.shapes.extraLarge,
                onClick = { },
                contentColor = MaterialTheme.colorScheme.onPrimary,
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = modifier
                    .height(52.dp)
                    .width(52.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add, contentDescription = "Floating action button."
                )
            }
        }

//        Spacer(modifier.height(20.dp))
    }
}

@Composable
private fun NavBtn(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: () -> Unit
) {
    Button(
        contentPadding = PaddingValues(7.dp),
        onClick = onClick,
        shape = MaterialTheme.shapes.extraLarge,
        colors = ButtonColors(
            containerColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
            contentColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = modifier
            .fillMaxHeight()
            .width(if (selected) 110.dp else 53.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = modifier
                .size(30.dp)
                .align(Alignment.CenterVertically)
        )
        if (selected) {
            Spacer(modifier.width(7.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge
            )
        }

    }

}

@Preview
@Composable
fun BottomNavigationBarPreview() {
    NFCTaskTheme {
        BottomBar(
            screenItems = listOf(
                HomeScreen.TaskList,
                HomeScreen.Statistics,
                HomeScreen.Profile
            ),
            currentScreenRoute = "statistics",
            onItemClick = {}
        )
    }
}