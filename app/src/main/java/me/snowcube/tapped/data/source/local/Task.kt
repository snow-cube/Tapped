package me.snowcube.tapped.data.source.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "task_title") val taskTitle: String,
    @ColumnInfo(name = "task_time") val taskTime: String,
    @ColumnInfo(name = "in_nfc_manner") val inNfcManner: Boolean,
    @ColumnInfo(name = "is_period") val isPeriod: Boolean,
)