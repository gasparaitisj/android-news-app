package com.telesoftas.justasonboardingapp.utils.firebase

import com.google.firebase.messaging.FirebaseMessagingService
import timber.log.Timber

class FirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        Timber.d("Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        //sendRegistrationToServer(token)
    }
}
