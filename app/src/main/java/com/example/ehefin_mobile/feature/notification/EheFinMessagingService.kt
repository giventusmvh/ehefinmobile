package com.example.ehefin_mobile.feature.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.ehefin_mobile.MainActivity
import com.example.ehefin_mobile.R
import com.example.ehefin_mobile.feature.loan.domain.repository.LoanRepository
import com.example.ehefin_mobile.feature.profile.domain.repository.ProfileRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Firebase Cloud Messaging Service for push notifications
 * Handles loan status updates and other notifications
 */
@AndroidEntryPoint
class EheFinMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var profileRepository: ProfileRepository

    @Inject
    lateinit var loanRepository: LoanRepository

    @Inject
    lateinit var plafondRepository: com.example.ehefin_mobile.feature.plafond.domain.repository.PlafondRepository

    // Use IoDispatcher if possible, but for simplicity here using IO direct or injecting dispatcher
    // Since I can't easily change constructor to add dispatcher without factory,
    // I'll stick to CoroutineScope with Dispatchers.IO for now or inject it if I had time to refactor.
    // However, I can inject the use case.

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        const val CHANNEL_ID = "ehefin_notifications"
        const val CHANNEL_NAME = "EheFin Notifications"
        const val CHANNEL_DESCRIPTION = "Notifikasi status pinjaman dan update penting"

        // Notification types from server
        const val TYPE_LOAN_STATUS = "LOAN_STATUS"
        const val TYPE_LOAN_APPROVED = "LOAN_APPROVED"
        const val TYPE_LOAN_REJECTED = "LOAN_REJECTED"
        const val TYPE_GENERAL = "GENERAL"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    /**
     * Called when FCM token is generated or refreshed
     * Send this token to your backend to enable push notifications for this device
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)

        serviceScope.launch {
            try {
                profileRepository.registerFcmToken(token)
                Log.d("FCM", "Token sent to backend: $token")
            } catch (e: Exception) {
                Log.e("FCM", "Failed to send token", e)
            }
        }
    }

    /**
     * Called when a message is received
     */
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        // Handle data payload
        val data = message.data
        val notificationType = data["type"] ?: TYPE_GENERAL
        val title = data["title"] ?: message.notification?.title ?: "EheFin"
        val body = data["body"] ?: message.notification?.body ?: ""
        val loanId = data["loanId"]

        // Trigger data refresh based on notification type
        if (loanId != null) {
            refreshLoanData(loanId.toLongOrNull())
        } else {
            // General refresh if loan ID not specified but it's a loan update
            if (notificationType.contains("LOAN")) {
                refreshLoanData(null)
            }
        }

        showNotification(
            title = title,
            message = body,
            notificationType = notificationType,
            loanId = loanId
        )
    }

    private fun refreshLoanData(loanId: Long?) {
        serviceScope.launch {
            try {
                // Refresh list (which also updates the loan detail in cache)
                loanRepository.refreshLoans()

                // Refresh plafond as loan changes might affect available limit
                try {
                    plafondRepository.refreshPlafond()
                } catch (e: Exception) {
                    Log.e("FCM", "Error refreshing plafond", e)
                }
                
                // Refresh specific history if ID available
                if (loanId != null) {
                    try {
                        loanRepository.refreshLoanHistory(loanId)
                    } catch (e: Exception) {
                        Log.e("FCM", "Error refreshing specific loan history", e)
                    }
                }
            } catch (e: Exception) {
                Log.e("FCM", "Error refreshing loan data", e)
            }
        }
    }

    private fun showNotification(
        title: String,
        message: String,
        notificationType: String,
        loanId: String?
    ) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create intent for when notification is tapped
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("notification_type", notificationType)
            loanId?.let { putExtra("loan_id", it) }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Choose icon based on notification type
        val icon = when (notificationType) {
            TYPE_LOAN_APPROVED -> android.R.drawable.ic_dialog_info
            TYPE_LOAN_REJECTED -> android.R.drawable.ic_dialog_alert
            TYPE_LOAN_STATUS -> android.R.drawable.ic_popup_sync
            else -> android.R.drawable.ic_dialog_info
        }

        val soundUri = Uri.parse("${ContentResolver.SCHEME_ANDROID_RESOURCE}://${packageName}/${R.raw.paimon_ehe}")



        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setSound(soundUri)
            .build()

        // Use unique ID for each notification (use loanId if available)
        val notificationId = loanId?.hashCode() ?: System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val soundUri = Uri.parse("${ContentResolver.SCHEME_ANDROID_RESOURCE}://${packageName}/${R.raw.paimon_ehe}")
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableLights(true)
                enableVibration(true)
                setSound(soundUri, audioAttributes)
            }


            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}