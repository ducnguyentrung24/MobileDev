package com.example.autoreplycall

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import android.telephony.TelephonyManager
import android.util.Log


class CallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)

        if (TelephonyManager.EXTRA_STATE_RINGING == state) {
            // Khi có cuộc gọi đến
            lastIncomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
            Log.d("CallReceiver", "Cuộc gọi đến từ: " + lastIncomingNumber)
        } else if (TelephonyManager.EXTRA_STATE_IDLE == state && lastState == TelephonyManager.EXTRA_STATE_RINGING) {
            // Nếu cuộc gọi kết thúc mà trước đó là RINGING (tức là cuộc gọi bị nhỡ)
            sendAutoReplySMS(context, lastIncomingNumber)
        }

        lastState = state
    }

    private fun sendAutoReplySMS(context: Context, phoneNumber: String?) {
        if (phoneNumber == null || phoneNumber.isEmpty()) return

        val message = "Xin chào, tôi hiện đang bận. Tôi sẽ gọi lại cho bạn sau!"
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(phoneNumber, null, message, null, null)
        Log.d("CallReceiver", "Đã gửi tin nhắn tự động đến: $phoneNumber")
    }

    companion object {
        private var lastState: String? = TelephonyManager.EXTRA_STATE_IDLE
        private var lastIncomingNumber: String? = ""
    }
}
