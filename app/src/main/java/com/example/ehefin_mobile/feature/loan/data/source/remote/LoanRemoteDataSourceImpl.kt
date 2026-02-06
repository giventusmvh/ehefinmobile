package com.example.ehefin_mobile.feature.loan.data.source.remote

import com.example.ehefin_mobile.core.common.DataResult
import com.example.ehefin_mobile.feature.loan.data.source.remote.dto.BranchDto
import com.example.ehefin_mobile.feature.loan.data.source.remote.dto.LoanHistoryDto
import com.example.ehefin_mobile.feature.loan.data.source.remote.dto.LoanRequestDto
import com.example.ehefin_mobile.feature.loan.data.source.remote.dto.LoanResponseDto
import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.Response
import javax.inject.Inject

/**
 * Implementation of LoanRemoteDataSource.
 * Wraps API calls with proper error handling and returns DataResult.
 */
class LoanRemoteDataSourceImpl @Inject constructor(
    private val loanApi: LoanApi,
    private val gson: Gson
) : LoanRemoteDataSource {

    override suspend fun getLoans(): DataResult<List<LoanResponseDto>> {
        return safeApiCall { loanApi.getLoans() }
    }

    override suspend fun getLoanById(loanId: Long): DataResult<LoanResponseDto> {
        return safeApiCall { loanApi.getLoanById(loanId) }
    }

    override suspend fun submitLoan(request: LoanRequestDto): DataResult<LoanResponseDto> {
        return safeApiCall { loanApi.submitLoan(request) }
    }

    override suspend fun getLoanHistory(loanId: Long): DataResult<List<LoanHistoryDto>> {
        return safeApiCall { loanApi.getLoanHistory(loanId) }
    }

    override suspend fun getBranches(): DataResult<List<BranchDto>> {
        return safeApiCall { loanApi.getBranches() }
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
