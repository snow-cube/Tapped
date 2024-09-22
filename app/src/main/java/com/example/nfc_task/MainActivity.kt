package com.example.nfc_task

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import com.example.nfc_task.tools.NfcManager
import com.example.nfc_task.ui.components.TaskApp
import com.example.nfc_task.ui.components.TaskAppHome
import com.example.nfc_task.ui.theme.NFCTaskTheme

class MainActivity : ComponentActivity() {

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

    public override fun onPause() {
        super.onPause()
        nfcManager?.writeModeOff(this)
    }

    public override fun onResume() {
        super.onResume()
        nfcManager?.writeModeOn(this, pendingIntent, writeTagFilters)
    }
}
