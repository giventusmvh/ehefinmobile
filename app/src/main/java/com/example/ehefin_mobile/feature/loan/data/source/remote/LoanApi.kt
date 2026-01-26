package com.example.ehefin_mobile.feature.loan.data.source.remote

import com.example.ehefin_mobile.core.common.Constants.Endpoints
import com.example.ehefin_mobile.core.network.ApiResponse
import com.example.ehefin_mobile.feature.loan.data.source.remote.dto.BranchDto
import com.example.ehefin_mobile.feature.loan.data.source.remote.dto.LoanHistoryDto
import com.example.ehefin_mobile.feature.loan.data.source.remote.dto.LoanRequestDto
import com.example.ehefin_mobile.feature.loan.data.source.remote.dto.LoanResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Retrofit API interface for Loan endpoints
 */
interface LoanApi {
    
    @GET(Endpoints.LOANS)
    suspend fun getLoans(): Response<ApiResponse<List<LoanResponseDto>>>
    
    @GET(Endpoints.LOAN_DETAIL)
    suspend fun getLoanById(@Path("id") loanId: Long): Response<ApiResponse<LoanResponseDto>>
    
    @POST(Endpoints.LOANS)
    suspend fun submitLoan(@Body request: LoanRequestDto): Response<ApiResponse<LoanResponseDto>>
    
    @GET(Endpoints.LOAN_HISTORY)
    suspend fun getLoanHistory(@Path("id") loanId: Long): Response<ApiResponse<List<LoanHistoryDto>>>
    
    @GET(Endpoints.BRANCHES)
    suspend fun getBranches(): Response<ApiResponse<List<BranchDto>>>
}
