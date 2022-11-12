package com.telesoftas.justasonboardingapp.utils.other

import android.os.Build

inline fun <T> sdkIs29AndUpOrNull(onSdk29: () -> T): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        onSdk29()
    } else null
}
