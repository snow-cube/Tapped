package com.example.nfc_task.tools

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.FormatException
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NdefRecord.createMime
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.widget.Toast
import java.io.IOException

class NfcManager(private val context: Context) {
    private val nfcAdapter: NfcAdapter? = NfcAdapter.getDefaultAdapter(context)
    val nfcAvailable = nfcAdapter != null

    // Check for available NFC Adapter
    init {
        if (!nfcAvailable) {
            Toast.makeText(context, "NFC is not available", Toast.LENGTH_LONG).show()
        }
    }

    private var writeMode = false // TODO: Make use of writeMode
    var myTag: Tag? = null
    private var msg: String? = null

    fun isNfcIntent(action: String): Boolean {
        return NfcAdapter.ACTION_TAG_DISCOVERED == action || NfcAdapter.ACTION_TECH_DISCOVERED == action || NfcAdapter.ACTION_NDEF_DISCOVERED == action
    }

    fun isNfcTagIntent(action: String): Boolean {
        return NfcAdapter.ACTION_TAG_DISCOVERED == action
    }

    // Read message from NFC Intent
    fun readFromIntent(intent: Intent) {
        intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)?.also { rawMsgs ->
            (rawMsgs[0] as NdefMessage).apply {
                // Record 0 contains the MIME type, record 1 is the AAR, if present
                msg = String(records[0].payload).split(' ')[0]
            }
        }
    }

    private fun createNdefMessage(text: String): NdefMessage {
        return NdefMessage(
            arrayOf(
                createMime(
                    "application/vnd.com.example.android.architecture.blueprints.main",
                    text.toByteArray()
                ),
                NdefRecord.createApplicationRecord("com.example.android.architecture.blueprints.main")
            )
        )
    }

    // Write to NFC Tag
    @Throws(IOException::class, FormatException::class)
    private fun write(text: String, tag: Tag?) {
        val message = createNdefMessage(text)
        // Get an instance of Ndef for the tag.
        val ndef = Ndef.get(tag)
        // Enable I/O
        ndef.connect()
        // Write the message
        ndef.writeNdefMessage(message)
        // Close the connection
        ndef.close()
    }

    fun writeToNfc(text: String) {
        try {
            if (myTag == null) {
                Toast.makeText(context, ERROR_DETECTED, Toast.LENGTH_LONG).show()
            } else {
                write(text, myTag)
                Toast.makeText(context, WRITE_SUCCESS, Toast.LENGTH_LONG).show()
            }
        } catch (e: IOException) {
            Toast.makeText(context, WRITE_ERROR, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        } catch (e: FormatException) {
            Toast.makeText(context, WRITE_ERROR, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    companion object {
        const val ERROR_DETECTED = "No NFC tag detected!"
        const val WRITE_SUCCESS = "Text written to the NFC tag successfully!"
        const val WRITE_ERROR = "Error during writing, is the NFC tag close enough to your device?"
    }

    // Enable Write and foreground dispatch to prevent intent-filter to launch the app again
    fun writeModeOn(
        activity: Activity,
        pendingIntent: PendingIntent?,
        writeTagFilters: Array<IntentFilter>
    ) {
        writeMode = true
        nfcAdapter?.enableForegroundDispatch(activity, pendingIntent, writeTagFilters, null)
    }

    //Disable Write and foreground dispatch to allow intent-filter to launch the app
    fun writeModeOff(activity: Activity) {
        writeMode = false
        nfcAdapter?.disableForegroundDispatch(activity)
    }
}