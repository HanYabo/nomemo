package com.han.nomemo

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat

class AiReanalysisForegroundService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val recordId = intent?.getStringExtra(EXTRA_RECORD_ID).orEmpty()
        if (recordId.isBlank()) {
            stopSelf(startId)
            return START_NOT_STICKY
        }

        val record = MemoryStore(applicationContext).findRecordById(recordId)
        if (record == null) {
            stopSelf(startId)
            return START_NOT_STICKY
        }

        val state = AiAnalysisStateJson.parse(record.aiAnalysisStateJson)
        val attempt = state?.attemptCount?.coerceAtLeast(1) ?: 1
        val attemptLimit = state?.attemptLimit?.coerceAtLeast(attempt) ?: attempt
        val notification = NoMemoLiveUpdateNotifier.buildAiAnalysisNotification(
            applicationContext,
            record,
            attempt,
            attemptLimit
        )
        ServiceCompat.startForeground(
            this,
            NoMemoLiveUpdateNotifier.aiAnalysisNotificationId(recordId),
            notification,
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            } else {
                0
            }
        )
        Log.d(TAG, "Foreground reanalysis protection started recordId=$recordId")
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        Log.d(TAG, "Foreground reanalysis protection stopped")
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val TAG = "AiReanalysisService"
        private const val EXTRA_RECORD_ID = "record_id"

        fun start(context: Context, recordId: String) {
            if (recordId.isBlank()) return
            val intent = Intent(context.applicationContext, AiReanalysisForegroundService::class.java)
                .putExtra(EXTRA_RECORD_ID, recordId)
            ContextCompat.startForegroundService(context.applicationContext, intent)
        }

        fun stop(context: Context) {
            context.applicationContext.stopService(
                Intent(context.applicationContext, AiReanalysisForegroundService::class.java)
            )
        }
    }
}
