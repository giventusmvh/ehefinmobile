package com.example.ehefin_mobile.feature.plafond.data.source.remote

import com.example.ehefin_mobile.core.common.DataResult
import com.example.ehefin_mobile.feature.plafond.data.source.remote.dto.PlafondResponseDto
import com.example.ehefin_mobile.feature.plafond.data.source.remote.dto.ProductResponseDto
import com.example.ehefin_mobile.feature.plafond.data.source.remote.dto.SelectPlafondRequest
import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.Response
import javax.inject.Inject

/**
 * Implementation of PlafondRemoteDataSource.
 * Wraps API calls with proper error handling and returns DataResult.
 */
class PlafondRemoteDataSourceImpl @Inject constructor(
    private val plafondApi: PlafondApi,
    private val gson: Gson
) : PlafondRemoteDataSource {

    override suspend fun getProducts(): DataResult<List<ProductResponseDto>> {
        return safeApiCall { plafondApi.getProducts() }
    }

    override suspend fun getActivePlafond(): DataResult<PlafondResponseDto> {
        return safeApiCall { plafondApi.getActivePlafond() }
    }

    override suspend fun selectPlafond(productId: Long): DataResult<PlafondResponseDto> {
        return safeApiCall { plafondApi.selectPlafond(SelectPlafondRequest(productId)) }
    }

    private suspend fun <T> safeApiCall(
        apiCall: suspend () -> Response<com.example.ehefin_mobile.core.network.ApiResponse<T>>
    ): DataResult<T> {
        return try {
            val response = apiCall()
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()!!.data?.let { DataResult.Success(it) }
                    ?: DataResult.Error("Data tidak ditemukan")
            } else {
                DataResult.Error(
                    message = parseErrorMessage(response),
                    code = response.code()
                )
            }
        } catch (e: Exception) {
            DataResult.Error(
                message = e.localizedMessage ?: "Terjadi kesalahan",
                throwable = e
            )
        }
    }

    private fun <T> parseErrorMessage(response: Response<T>): String {
        return try {
            val errorBody = response.errorBody()?.string()
            if (errorBody != null) {
                val jsonObject = gson.fromJson(errorBody, JsonObject::class.java)
                jsonObject.get("message")?.asString ?: "Terjadi kesalahan"
            } else {
                "Terjadi kesalahan"
            }
        } catch (e: Exception) {
            "Terjadi kesalahan"
        }
    }
}