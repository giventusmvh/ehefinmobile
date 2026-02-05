package com.example.ehefin_mobile.feature.profile.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity for Profile - cached for offline-first
 */
@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey
    val userId: Long,
    val name: String,
    val email: String,
    val nik: String?,
    val birthdate: String?,
    val phoneNumber: String?,
    val address: String?,
    val ktpPath: String?,
    val kkPath: String?,
    val npwpPath: String?,
    val bankName: String?,
    val accountNumber: String?,
    val accountHolderName: String?,
    val job: String? = null,
    val companyName: String? = null,
    val selfiePath: String? = null,
    val salarySlipPath: String? = null,
    val isComplete: Boolean = false,
    val lastSyncedAt: Long = System.currentTimeMillis()
)