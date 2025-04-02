package com.example.callblocker

import android.telecom.Call
import android.telecom.CallScreeningService

class CallBlocker : CallScreeningService() {
    override fun onScreenCall(callDetails: Call.Details) {
        val incomingNumber = callDetails.handle?.schemeSpecificPart ?: return

        if (isBlockedNumber(incomingNumber)) {
            val response = CallResponse.Builder()
                .setDisallowCall(true)
                .setSkipCallLog(true)
                .setSkipNotification(true)
                .build()
            respondToCall(callDetails, response)
        }
    }

    private fun isBlockedNumber(number: String): Boolean {
        val blockedNumbers = listOf("+84398269310", "0384719169", "0876940967", "+84876940967")
        return blockedNumbers.contains(number)
    }
}
