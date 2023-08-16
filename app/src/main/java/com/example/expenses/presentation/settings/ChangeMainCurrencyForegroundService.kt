package com.example.expenses.presentation.settings

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.lifecycle.Observer
import com.example.expenses.R
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
        const val ACTION_CHANGE_MAIN_CURRENCY = "ACTION_CHANGE_MAIN_CURRENCY"
        const val ACTION_CHANGE_SECONDARY_CURRENCIES = "ACTION_CHANGE_SECONDARY_CURRENCIES"
        const val CURRENCY_TO_TAG = "CURRENCY_TO_TAG"
        const val CURRENCIES_TO_TAG = "CURRENCIES_TO_TAG"
        private const val NOTIFICATION_CHANNEL_ID = "Changing currencies"
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
                getString(R.string.changing_currencies)
            )
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        observer = Observer<String> { notificationManager.notifyWithLoading(NOTIFICATION_ID, NOTIFICATION_CHANNEL_ID, it) }
        settingsRepository.stateChangedLiveData.observeForever(observer)
        CoroutineScope(Dispatchers.IO + job).launch {
            intent!!.let {
                when (intent.action!!){
                    ACTION_CHANGE_MAIN_CURRENCY -> settingsRepository.changeMainCurrency(intent.getStringExtra(CURRENCY_TO_TAG)!!)
                    ACTION_CHANGE_SECONDARY_CURRENCIES -> settingsRepository.changeSecondaryCurrencies(intent.getStringArrayListExtra(CURRENCIES_TO_TAG)!!)
                }
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        notificationManager.cancel(NOTIFICATION_ID)
        settingsRepository.stateChangedLiveData.removeObserver(observer)
        job.cancel()
        super.onDestroy()
    }
}