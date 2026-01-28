package com.example.ehefin_mobile.feature.plafond.data.mapper

import com.example.ehefin_mobile.feature.plafond.data.source.local.PlafondEntity
import com.example.ehefin_mobile.feature.plafond.data.source.local.ProductEntity
import com.example.ehefin_mobile.feature.plafond.data.source.remote.dto.PlafondResponseDto
import com.example.ehefin_mobile.feature.plafond.data.source.remote.dto.ProductResponseDto
import com.example.ehefin_mobile.feature.plafond.domain.model.Product
import com.example.ehefin_mobile.feature.plafond.domain.model.UserPlafond

// Product Mappers
fun ProductResponseDto.toEntity(): ProductEntity {
    return ProductEntity(
            id = id,
            name = name,
            amount = amount,
            tenor = tenor,
            interestRate = interestRate
    )
}

fun ProductEntity.toDomain(): Product {
    return Product(
            id = id,
            name = name,
            amount = amount,
            tenor = tenor,
            interestRate = interestRate
    )
}

fun ProductResponseDto.toDomain(): Product {
    return toEntity().toDomain()
}

// Plafond Mappers
fun PlafondResponseDto.toEntity(): PlafondEntity {
    return PlafondEntity(
            id = id,
            userId = 0L, // API doesn't return userId, will be set from session if needed
            productId = product.id,
            productName = product.name,
            originalAmount = originalAmount,
            remainingAmount = remainingAmount,
            tenor = product.tenor,
            interestRate = product.interestRate,
            assignedAt = assignedAt,
            isActive = isActive
    )
}

fun PlafondEntity.toDomain(): UserPlafond {
    return UserPlafond(
            id = id,
            userId = userId,
            productId = productId,
            productName = productName,
            originalAmount = originalAmount,
            remainingAmount = remainingAmount,
            tenor = tenor,
            interestRate = interestRate,
            assignedAt = assignedAt,
            isActive = isActive
    )
}

fun PlafondResponseDto.toDomain(): UserPlafond {
    return toEntity().toDomain()
}