package com.example.nfc_task

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.nfc.NfcAdapter
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.nfc_task.tools.NfcManager
import com.example.nfc_task.ui.components.TaskApp
import com.example.nfc_task.ui.theme.NFCTaskTheme

class MainActivity : ComponentActivity() {

    private var taskService: TaskService? = null
    private var isBound = false

    // ServiceConnection 用于监听服务的连接和断开
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as TaskService.TaskBinder
            taskService = binder.getService()  // 获取到 TaskService 的实例
            isBound = true
            Log.d("MainActivity", "Service connected")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            taskService = null
            isBound = false
            Log.d("MainActivity", "Service disconnected")
        }
    }

    private lateinit var writeTagFilters: Array<IntentFilter>
    private var pendingIntent: PendingIntent? = null
    private var nfcManager: NfcManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NFCTaskTheme {
                TaskApp()
            }
        }

        nfcManager = NfcManager(context = this)

        // For when the activity is launched by the intent-filter for android.nfc.action.NDEF_DISCOVERED
        if (nfcManager?.isNfcIntent(intent.action ?: "") == true) {
            nfcManager?.readFromIntent(intent)
        } else {
            // Other type of intent
        }

        pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_MUTABLE // Important!!!
        )
        val tagDetected = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT)
        writeTagFilters = arrayOf(tagDetected)

//        if (isBound) {
//            taskService?.startTask()
//        }
    }

    // For reading the NFC when the app is already launched
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        nfcManager?.readFromIntent(intent)
        if (nfcManager?.isNfcTagIntent((intent.action ?: "")) == true) {
            nfcManager?.myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        }
    }

    override fun onStart() {
        super.onStart()

        Intent(this, TaskService::class.java).also { intent ->
            bindService(intent, connection, 0)
        }
    }

    public override fun onPause() {
        super.onPause()
        nfcManager?.writeModeOff(this)
    }

    public override fun onResume() {
        super.onResume()
        nfcManager?.writeModeOn(this, pendingIntent, writeTagFilters)
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
    }
}
