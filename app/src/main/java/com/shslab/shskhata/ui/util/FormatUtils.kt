package com.shslab.shskhata.ui.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Format a monetary amount in Bangladeshi Taka style.
 * e.g. 1250.5 -> "৳ 1,250.50"
 */
fun formatTaka(amount: Double): String {
    val formatter = java.text.NumberFormat.getNumberInstance(Locale.getDefault())
    formatter.maximumFractionDigits = 2
    formatter.minimumFractionDigits = 0
    val formatted = formatter.format(amount)
    return "৳ $formatted"
}

/**
 * Format a timestamp as a readable date string, e.g. "০৩ অক্টো ২০২৬, ৪:৩০ PM".
 * Falls back to standard format on unsupported locales.
 */
fun formatDate(timestamp: Long): String {
    val date = Date(timestamp)
    val formatter = try {
        SimpleDateFormat("d MMM yyyy, h:mm a", Locale.getDefault())
    } catch (e: Exception) {
        SimpleDateFormat("dd/MM/yyyy, hh:mm a", Locale.getDefault())
    }
    return formatter.format(date)
}
