package com.example.ehefin_mobile.core.common

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Extension functions for common operations */

// String Extensions
fun String?.orEmpty(): String = this ?: ""

fun String?.isNotNullOrEmpty(): Boolean = !this.isNullOrEmpty()

// Currency Formatting (Indonesian Rupiah)
fun Double.formatToRupiah(): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return format.format(this).replace(",00", "")
}

fun Long.formatToRupiah(): String {
    return this.toDouble().formatToRupiah()
}

// Alias for convenience
fun Double.toCurrencyFormat(): String = this.formatToRupiah()

fun Long.toCurrencyFormat(): String = this.formatToRupiah()

// Date Formatting
fun String.toDisplayDate(): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
        val date = inputFormat.parse(this)
        date?.let { outputFormat.format(it) } ?: this
    } catch (e: Exception) {
        this
    }
}

fun String.toSimpleDate(): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        val date = inputFormat.parse(this)
        date?.let { outputFormat.format(it) } ?: this
    } catch (e: Exception) {
        this
    }
}

fun Date.toApiFormat(): String {
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return format.format(this)
}

// Phone Number Formatting
fun String.formatPhoneNumber(): String {
    return if (this.startsWith("0")) {
        "+62${this.substring(1)}"
    } else if (!this.startsWith("+")) {
        "+62$this"
    } else {
        this
    }
}

// NIK Validation
fun String.isValidNik(): Boolean {
    return this.length == 16 && this.all { it.isDigit() }
}
