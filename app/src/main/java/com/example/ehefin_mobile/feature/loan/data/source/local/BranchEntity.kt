package com.example.ehefin_mobile.feature.loan.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity for Branch
 */
@Entity(tableName = "branches")
data class BranchEntity(
    @PrimaryKey
    val id: Long,
    val code: String,
    val name: String
)
