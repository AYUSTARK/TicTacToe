package com.ayustark.tictactoe

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class Notifications {

    private val NOTIFIYTAG = "new request"
    fun notify(context: Context, message: String, number: Int) {
        val intent = Intent(context, Login::class.java)

        val builder = NotificationCompat.Builder(context, NOTIFIYTAG)
            .setDefaults(Notification.DEFAULT_ALL)
            .setContentTitle("New request")
            .setContentText(message)
            .setNumber(number)
            .setSmallIcon(R.drawable.tictac)
            .setContentIntent(
                PendingIntent.getActivity(
                    context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            .setAutoCancel(true)
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(NOTIFIYTAG, 0, builder.build())
    }
}
