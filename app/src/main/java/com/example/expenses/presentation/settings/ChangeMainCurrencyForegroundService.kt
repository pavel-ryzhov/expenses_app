package com.example.expenses.presentation.settings

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.lifecycle.Observer
import com.example.expenses.data.repository.settings.SettingsRepository
import com.example.expenses.utils.NotificationManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChangeMainCurrencyForegroundService : Service() {

    companion object {
        const val CURRENCY_TO_TAG = "CURRENCY_TO_TAG"
        private const val NOTIFICATION_CHANNEL_ID = "Changing main currency"
        private const val NOTIFICATION_ID = 1
    }

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var settingsRepository: SettingsRepository
    private val job = Job()
    private lateinit var observer: Observer<String>

    override fun onCreate() {
        super.onCreate()
        startForeground(
            NOTIFICATION_ID,
            notificationManager.notifyWithLoading(
                NOTIFICATION_ID,
                NOTIFICATION_CHANNEL_ID,
                "Fetching new exchange rates..."
            )
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        observer = Observer<String> { notificationManager.notifyWithLoading(NOTIFICATION_ID, NOTIFICATION_CHANNEL_ID, it) }
        settingsRepository.stateChangedLiveData.observeForever(observer)
        CoroutineScope(Dispatchers.IO + job).launch {
            settingsRepository.changeMainCurrency(intent!!.getStringExtra(CURRENCY_TO_TAG)!!)
            stopSelf()
        }
        return START_NOT_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        settingsRepository.stateChangedLiveData.removeObserver(observer)
        job.cancel()
    }
}