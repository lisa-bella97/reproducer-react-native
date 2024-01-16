package com.reproducerapp

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.util.Consumer
import com.facebook.react.ReactActivity
import com.facebook.react.ReactActivityDelegate
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint.fabricEnabled
import com.facebook.react.defaults.DefaultReactActivityDelegate

private const val LOG_TAG = "MY_LOGS"

private val PENDING_INTENT_FLAGS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else 0

class MainActivity : ReactActivity() {
  private val newIntentListener = Consumer<Intent> { handleNfcIntent(it) }

  /**
   * Returns the name of the main component registered from JavaScript. This is used to schedule
   * rendering of the component.
   */
  override fun getMainComponentName(): String = "ReproducerApp"

  /**
   * Returns the instance of the [ReactActivityDelegate]. We use [DefaultReactActivityDelegate]
   * which allows you to enable New Architecture with a single boolean flags [fabricEnabled]
   */
  override fun createReactActivityDelegate(): ReactActivityDelegate =
      DefaultReactActivityDelegate(this, mainComponentName, fabricEnabled)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Log.d(LOG_TAG, "MainActivity addOnNewIntentListener")
    addOnNewIntentListener(newIntentListener)
  }

  override fun onResume() {
    super.onResume()
    val nfcIntent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
    val pendingIntent = PendingIntent.getActivity(this, 0, nfcIntent, PENDING_INTENT_FLAGS)
    NfcAdapter.getDefaultAdapter(this)?.let {
      Log.d(LOG_TAG, "MainActivity enableForegroundDispatch")
      it.enableForegroundDispatch(this, pendingIntent, null, null)
    }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    Log.d(LOG_TAG, "MainActivity onNewIntent $intent")
  }

  override fun onPause() {
    super.onPause()
    NfcAdapter.getDefaultAdapter(this)?.let {
      Log.d(LOG_TAG, "MainActivity disableForegroundDispatch")
      it.disableForegroundDispatch(this)
    }
  }

  override fun onDestroy() {
    Log.d(LOG_TAG, "MainActivity removeOnNewIntentListener")
    removeOnNewIntentListener(newIntentListener)
    super.onDestroy()
  }

  // This callback is not called (although it should) when the Activity's onNewIntent occurs.
  private fun handleNfcIntent(intent: Intent) {
    Log.d(LOG_TAG, "MainActivity handleNfcIntent $intent")
  }
}
