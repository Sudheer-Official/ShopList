package uk.ac.tees.mad.shoplist.ui.utils

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import uk.ac.tees.mad.shoplist.R
import kotlin.random.Random

fun showNotification(context: Context, title: String, message: String) {
    val builder = NotificationCompat.Builder(context, "list_channel")
        .setSmallIcon(R.drawable.ic_shoplist_logo).setContentTitle(title).setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    with(NotificationManagerCompat.from(context)) {
        try {
            val notificationId = Random.nextInt()
            notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            Log.e("Notification", "Notification permission not granted: ${e.message}")
        }
    }
}