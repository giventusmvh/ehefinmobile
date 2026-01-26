package com.example.ehefin_mobile.feature.plafond.data.source.remote

import com.example.ehefin_mobile.core.network.ApiResponse
import com.example.ehefin_mobile.feature.plafond.data.source.remote.dto.PlafondResponseDto
import com.example.ehefin_mobile.feature.plafond.data.source.remote.dto.ProductResponseDto
import com.example.ehefin_mobile.feature.plafond.data.source.remote.dto.SelectPlafondRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/** Retrofit API interface for Plafond endpoints */
interface PlafondApi {

    @GET("products")
    suspend fun getProducts(): Response<ApiResponse<List<ProductResponseDto>>>

    @GET("customer/plafond")
    suspend fun getActivePlafond(): Response<ApiResponse<PlafondResponseDto>>

    @POST("customer/plafond")
    suspend fun selectPlafond(
            @Body request: SelectPlafondRequest
    ): Response<ApiResponse<PlafondResponseDto>>
}
