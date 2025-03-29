package uk.ac.tees.mad.shoplist.ui.utils

import java.text.SimpleDateFormat
import java.util.Locale

fun getCurrentDateAndTime(): String {
    val sdf = SimpleDateFormat("MMMM d, yyyy | hh:mm a", Locale.getDefault())
    return sdf.format(System.currentTimeMillis())
}