package com.example.expenses.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.expenses.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotificationManager @Inject constructor(
    @ApplicationContext
    private val context: Context
) {

    private val notificationManager = context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager

    fun notifyWithLoading(notificationId: Int, channelId: String, text: String): Notification{
        val builder =
            NotificationCompat.Builder(context, channelId)
                .setContentTitle(text)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setProgress(100, 50, true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(channelId)
            val nc =
                NotificationChannel(
                    channelId,
                    channelId,
                    NotificationManager.IMPORTANCE_HIGH
                )
            notificationManager.createNotificationChannel(nc)
        }
        val notification = builder.build()
        notificationManager.notify(notificationId, notification)
        return notification
    }

    fun notify(notificationId: Int, channelId: String, text: String): Notification{
        val builder =
            NotificationCompat.Builder(context, channelId)
                .setContentTitle(text)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(channelId)
            val nc =
                NotificationChannel(
                    channelId,
                    channelId,
                    NotificationManager.IMPORTANCE_HIGH
                )
            notificationManager.createNotificationChannel(nc)
        }
        val notification = builder.build()
        notificationManager.notify(notificationId, notification)
        return notification
    }

    fun cancel(notificationId: Int){
        notificationManager.cancel(notificationId)
    }
}