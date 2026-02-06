package com.example.ehefin_mobile.feature.auth.data.source.remote

import com.example.ehefin_mobile.core.common.DataResult
import com.example.ehefin_mobile.feature.auth.data.source.remote.dto.AuthResponseDto
import com.example.ehefin_mobile.feature.auth.data.source.remote.dto.ForgotPasswordRequest
import com.example.ehefin_mobile.feature.auth.data.source.remote.dto.FirebaseLoginRequest
import com.example.ehefin_mobile.feature.auth.data.source.remote.dto.LoginRequest
import com.example.ehefin_mobile.feature.auth.data.source.remote.dto.RegisterRequest
import com.example.ehefin_mobile.feature.auth.data.source.remote.dto.ResetPasswordRequest
import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.Response
import javax.inject.Inject

/**
 * Implementation of AuthRemoteDataSource.
 * Wraps API calls with proper error handling and returns DataResult.
 */
class AuthRemoteDataSourceImpl @Inject constructor(
    private val authApi: AuthApi,
    private val gson: Gson
) : AuthRemoteDataSource {

    override suspend fun login(email: String, password: String): DataResult<AuthResponseDto> {
        return safeApiCall {
            authApi.login(LoginRequest(email, password))
        }
    }

    override suspend fun register(
        name: String,
        email: String,
        password: String
    ): DataResult<AuthResponseDto> {
        return safeApiCall {
            authApi.register(RegisterRequest(name, email, password))
        }
    }

    override suspend fun logout(): DataResult<Unit> {
        return try {
            authApi.logout()
            // Always return success since we want to clear local data anyway
            DataResult.Success(Unit)
        } catch (e: Exception) {
            // Even on error, consider logout successful for local cleanup
            DataResult.Success(Unit)
        }
    }

    override suspend fun forgotPassword(email: String): DataResult<Unit> {
        return try {
            val response = authApi.forgotPassword(ForgotPasswordRequest(email))
            // Always return success for security (don't reveal if email exists)
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(
                message = e.localizedMessage ?: "Terjadi kesalahan",
                throwable = e
            )
        }
    }

    override suspend fun resetPassword(
        token: String,
        newPassword: String,
        confirmPassword: String
    ): DataResult<Unit> {
        return try {
            val response = authApi.resetPassword(
                ResetPasswordRequest(token, newPassword, confirmPassword)
            )
            if (response.isSuccessful && response.body()?.success == true) {
                DataResult.Success(Unit)
            } else {
                DataResult.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            DataResult.Error(
                message = e.localizedMessage ?: "Terjadi kesalahan",
                throwable = e
            )
        }
    }

    override suspend fun loginWithFirebase(
        idToken: String,
        fcmToken: String?
    ): DataResult<AuthResponseDto> {
        return safeApiCall {
            authApi.firebaseLogin(FirebaseLoginRequest(idToken, fcmToken))
        }
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
