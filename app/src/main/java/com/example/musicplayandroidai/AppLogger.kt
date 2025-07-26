package com.example.musicplayandroidai

object AppLogger {
    private const val TAG = "MusicplayLog"

    fun d(message: String) = android.util.Log.d(TAG, message)
    fun i(message: String) = android.util.Log.i(TAG, message)
    fun e(message: String, throwable: Throwable? = null) {
        if (throwable != null) android.util.Log.e(TAG, message, throwable)
        else android.util.Log.e(TAG, message)
    }
}
