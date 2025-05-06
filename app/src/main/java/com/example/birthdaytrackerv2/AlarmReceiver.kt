package com.example.birthdaytrackerv2

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class AlarmReceiver : BroadcastReceiver() {
    private val CHANNEL_ID = "channel_id"

    override fun onReceive(context: Context, intent: Intent) {
        sendNotification(context, intent)

        val eventId = intent.getIntExtra("event_id", -1)
        val eventDay = intent.getIntExtra("event_day", 1)
        val eventMonth = intent.getIntExtra("event_month", 0)
        val eventName = intent.getStringExtra("event_name")
        if (eventId != -1) {
            ReminderManager.setAlarm(context, eventDay, eventMonth, eventId, eventName!!)
        }
    }

    private fun sendNotification(context: Context, event: Intent) {
        val event_day = event.getIntExtra("event_day", 1)
        val event_month = event.getIntExtra("event_month", 0)
        val event_name = event.getStringExtra("event_name")

        val notificationMessage = StringBuilder()
        notificationMessage.append(event_day)
        notificationMessage.append("/")
        notificationMessage.append(event_month)
        notificationMessage.append(": ")
        notificationMessage.append(event_name)
        notificationMessage.append("!")

        val intent = Intent(context, StartActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
            .setContentTitle("Намечается день рождения!")
            .setContentText(notificationMessage)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle())

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@with
            }
            notify(123, builder.build())
        }
    }
}