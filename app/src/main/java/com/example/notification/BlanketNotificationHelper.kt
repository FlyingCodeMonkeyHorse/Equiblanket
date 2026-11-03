package com.example.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.data.model.BlanketRecommendation

object BlanketNotificationHelper {
    const val CHANNEL_ID = "horse_blanket_alerts"
    const val CHANNEL_NAME = "Evening Horse Blanket Alerts"
    private const val NOTIFICATION_ID = 1001

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifies you when your horse needs to be blanketed for the evening based on the weather forecast"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 150, 300)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            manager?.createNotificationChannel(channel)
        }
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun sendBlanketAlert(
        context: Context,
        recommendation: BlanketRecommendation,
        notificationId: Int = NOTIFICATION_ID
    ) {
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = if (recommendation.needsBlanket) {
            "🏇 Blanket Alert: ${recommendation.horseName} Tonight"
        } else {
            "🏇 Evening Check: No Blanket Needed for ${recommendation.horseName}"
        }

        val contentText = "${recommendation.blanketType.shortName} • Low: ${recommendation.eveningLowF.toInt()}°F"

        val bigText = buildString {
            append(recommendation.rationale)
            append("\n\nOvernight Low: ${recommendation.eveningLowF.toInt()}°F (Feels like: ${recommendation.overnightApparentLowF.toInt()}°F)")
            if (recommendation.rainRisk) {
                append(" • Precipitation Expected")
            }
            if (recommendation.windWarning) {
                append(" • High Wind Warning")
            }
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        try {
            manager?.notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            // Missing POST_NOTIFICATIONS on Android 13+
        }
    }
}
