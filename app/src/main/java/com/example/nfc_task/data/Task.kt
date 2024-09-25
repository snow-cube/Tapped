package com.example.nfc_task.data

import com.example.nfc_task.ui.theme.StateColor
import kotlin.random.Random

class Task(
    val taskName: String,
    val taskTime: String,
    val inNfcManner: Boolean = true,
    val isPeriod: Boolean = false,
    val isRepeat: Boolean = false,
) {
    private val values = enumValues<StateColor>()
    val stateColor = values[Random.nextInt(values.size)].color
}