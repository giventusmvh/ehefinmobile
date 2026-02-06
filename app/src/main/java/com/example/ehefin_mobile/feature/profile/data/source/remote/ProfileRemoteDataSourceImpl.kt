package com.example.ehefin_mobile.feature.profile.data.source.remote

import com.example.ehefin_mobile.core.common.DataResult
import com.example.ehefin_mobile.feature.profile.data.source.remote.dto.FcmTokenRequest
import com.example.ehefin_mobile.feature.profile.data.source.remote.dto.ProfileResponseDto
import com.example.ehefin_mobile.feature.profile.data.source.remote.dto.UpdateProfileRequest
import com.example.ehefin_mobile.feature.profile.data.source.remote.dto.UpdateProfileResponseDto
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject

/**
 * Implementation of ProfileRemoteDataSource.
 * Wraps API calls with proper error handling and returns DataResult.
 */
class ProfileRemoteDataSourceImpl @Inject constructor(
    private val profileApi: ProfileApi,
    private val gson: Gson
) : ProfileRemoteDataSource {

    override suspend fun getProfile(): DataResult<ProfileResponseDto> {
        return safeApiCall { profileApi.getProfile() }
    }

    override suspend fun updateProfile(
        nik: String,
        phoneNumber: String,
        address: String,
        bankName: String,
        accountNumber: String,
        accountHolderName: String,
        birthdate: String,
        job: String,
        companyName: String,
        ktpFile: File?,
        kkFile: File?,
        npwpFile: File?,
        selfieFile: File?,
        salarySlipFile: File?
    ): DataResult<UpdateProfileResponseDto> {
        return try {
            // Create JSON data part
            val dataJson = gson.toJson(
                UpdateProfileRequest(
                    nik = nik,
                    phone = phoneNumber,
                    address = address,
                    bankName = bankName,
                    accountNumber = accountNumber,
                    accountHolderName = accountHolderName,
                    birthdate = birthdate,
                    job = job,
                    companyName = companyName
                )
            )
            val dataPart = dataJson.toRequestBody("application/json".toMediaTypeOrNull())

            // Create file parts
            val ktpPart = ktpFile?.toMultipartPart("ktp")
            val kkPart = kkFile?.toMultipartPart("kk")
            val npwpPart = npwpFile?.toMultipartPart("npwp")
            val selfiePart = selfieFile?.toMultipartPart("selfie")
            val salarySlipPart = salarySlipFile?.toMultipartPart("salarySlip")

            val response = profileApi.updateProfile(
                dataPart, ktpPart, kkPart, npwpPart, selfiePart, salarySlipPart
            )

            if (response.isSuccessful && response.body()?.success == true) {
                response.body()!!.data?.let { DataResult.Success(it) }
                    ?: DataResult.Error("Data tidak ditemukan")
            } else {
                DataResult.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            DataResult.Error(
                message = e.localizedMessage ?: "Gagal update profil",
                throwable = e
            )
        }
    }

    override suspend fun registerFcmToken(token: String): DataResult<Unit> {
        return try {
            val response = profileApi.registerFcmToken(FcmTokenRequest(token))
            if (response.isSuccessful && response.body()?.success == true) {
                DataResult.Success(Unit)
            } else {
                DataResult.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            DataResult.Error(
                message = e.localizedMessage ?: "Gagal register FCM token",
                throwable = e
            )
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
                val message = jsonObject.get("message")?.asString ?: "Terjadi kesalahan"

                val errors = jsonObject.get("errors")?.asJsonArray
                if (errors != null && errors.size() > 0) {
                    val errorList = errors.map { it.asString.replace("\"", "") }.joinToString("\n")
                    "$message:\n$errorList"
                } else {
                    message
                }
            } else {
                "Terjadi kesalahan"
            }
        } catch (e: Exception) {
            "Terjadi kesalahan"
        }
    }

    private fun File.toMultipartPart(name: String): MultipartBody.Part {
        val requestFile = this.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(name, this.name, requestFile)
    }
}