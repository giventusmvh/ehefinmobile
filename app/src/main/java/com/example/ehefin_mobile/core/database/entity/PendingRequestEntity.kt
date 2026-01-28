package com.example.ehefin_mobile.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_requests")
data class PendingRequestEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String, // e.g., "SUBMIT_LOAN"
    val data: String, // JSON payload
    val createdAt: Long = System.currentTimeMillis(),
    val status: String = "PENDING" // PENDING, FAILED, RETRYING
)