package com.example.ehefin_mobile.feature.profile.data.repository

import com.example.ehefin_mobile.core.common.Resource
import com.example.ehefin_mobile.core.datastore.TokenManager
import com.example.ehefin_mobile.core.di.IoDispatcher
import com.example.ehefin_mobile.core.network.NetworkMonitor
import com.example.ehefin_mobile.feature.profile.data.mapper.toDomain
import com.example.ehefin_mobile.feature.profile.data.mapper.toEntity
import com.example.ehefin_mobile.feature.profile.data.source.local.ProfileDao
import com.example.ehefin_mobile.feature.profile.data.source.local.ProfileEntity
import com.example.ehefin_mobile.feature.profile.data.source.remote.ProfileApi
import com.example.ehefin_mobile.feature.profile.data.source.remote.dto.FcmTokenRequest
import com.example.ehefin_mobile.feature.profile.data.source.remote.dto.UpdateProfileRequest
import com.example.ehefin_mobile.feature.profile.domain.model.UserProfile
import com.example.ehefin_mobile.feature.profile.domain.repository.ProfileRepository
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response

class ProfileRepositoryImpl
@Inject
constructor(
        private val profileApi: ProfileApi,
        private val profileDao: ProfileDao,
        private val tokenManager: TokenManager,
        private val networkMonitor: NetworkMonitor,
        private val gson: Gson,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ProfileRepository {

    override fun getProfile(): Flow<Resource<UserProfile>> =
            flow {
                        emit(Resource.Loading())

                        // 1. Get userId from session
                        val userId = tokenManager.getUserId().first()

                        if (userId != null) {
                            // 2. Emit cached data
                            val cachedProfile = profileDao.getProfileByUserIdSync(userId)
                            if (cachedProfile != null) {
                                emit(Resource.Success(cachedProfile.toDomain()))
                            }

                            // 3. Fetch from network
                            if (networkMonitor.isOnline()) {
                                try {
                                    val response = profileApi.getProfile()
                                    if (response.isSuccessful && response.body()?.success == true) {
                                        val profileDto = response.body()!!.data!!
                                        // Update cache
                                        profileDao.insertProfile(profileDto.toEntity())
                                        emit(Resource.Success(profileDto.toDomain()))
                                    } else {
                                        val error = parseErrorMessage(response)
                                        if (cachedProfile != null) {
                                            emit(Resource.Error(error, cachedProfile.toDomain()))
                                        } else {
                                            emit(Resource.Error(error))
                                        }
                                    }
                                } catch (e: Exception) {
                                    val error = "Gagal memuat profil: ${e.localizedMessage}"
                                    if (cachedProfile != null) {
                                        emit(Resource.Error(error, cachedProfile.toDomain()))
                                    } else {
                                        emit(Resource.Error(error))
                                    }
                                }
                            } else {
                                if (cachedProfile == null) {
                                    emit(Resource.Error("Tidak ada koneksi internet"))
                                }
                            }
                        } else {
                            emit(Resource.Error("User ID tidak ditemukan"))
                        }
                    }
                    .flowOn(ioDispatcher)

    override suspend fun updateProfile(
            nik: String,
            phoneNumber: String,
            address: String,
            bankName: String,
            accountNumber: String,
            accountHolderName: String,
            birthdate: String,
            ktpFile: File?,
            kkFile: File?,
            npwpFile: File?
    ): Resource<UserProfile> {
        if (!networkMonitor.isOnline()) {
            return Resource.Error("Tidak ada koneksi internet")
        }

        return try {
            // Get existing profile for userId, name, email (not returned in update response)
            val userId = tokenManager.getUserId().first()
            val existingProfile =
                    if (userId != null) {
                        profileDao.getProfileByUserIdSync(userId)
                    } else null

            if (existingProfile == null) {
                return Resource.Error("Profil tidak ditemukan")
            }

            // Create JSON data part
            val dataJson =
                    gson.toJson(
                            UpdateProfileRequest(
                                    nik = nik,
                                    phone = phoneNumber,
                                    address = address,
                                    bankName = bankName,
                                    accountNumber = accountNumber,
                                    accountHolderName = accountHolderName,
                                    birthdate = birthdate
                            )
                    )
            val dataPart = dataJson.toRequestBody("application/json".toMediaTypeOrNull())

            // Create file parts
            val ktpPart =
                    ktpFile?.let { file ->
                        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                        MultipartBody.Part.createFormData("ktp", file.name, requestFile)
                    }

            val kkPart =
                    kkFile?.let { file ->
                        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                        MultipartBody.Part.createFormData("kk", file.name, requestFile)
                    }

            val npwpPart =
                    npwpFile?.let { file ->
                        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                        MultipartBody.Part.createFormData("npwp", file.name, requestFile)
                    }

            val response = profileApi.updateProfile(dataPart, ktpPart, kkPart, npwpPart)

            if (response.isSuccessful && response.body()?.success == true) {
                val profileDto = response.body()!!.data!!
                val userProfile =
                        profileDto.toDomain(
                                existingUserId = existingProfile.userId,
                                existingName = existingProfile.name,
                                existingEmail = existingProfile.email
                        )
                // Update cache with new profile data merged with existing
                profileDao.insertProfile(
                        ProfileEntity(
                                userId = existingProfile.userId,
                                name = existingProfile.name,
                                email = existingProfile.email,
                                nik = profileDto.nik,
                                birthdate = profileDto.birthdate,
                                phoneNumber = profileDto.phoneNumber,
                                address = profileDto.address,
                                ktpPath = profileDto.ktpUrl,
                                kkPath = profileDto.kkUrl,
                                npwpPath = profileDto.npwpUrl,
                                bankName = profileDto.bankName,
                                accountNumber = profileDto.accountNumber,
                                accountHolderName = profileDto.accountHolderName,
                                isComplete = profileDto.isComplete ?: false
                        )
                )
                Resource.Success(userProfile)
            } else {
                Resource.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            Resource.Error("Gagal update profil: ${e.localizedMessage}")
        }
    }

    override suspend fun registerFcmToken(token: String): Resource<Unit> {
        if (!networkMonitor.isOnline()) {
            return Resource.Error("Tidak ada koneksi internet")
        }

        return try {
            val response = profileApi.registerFcmToken(FcmTokenRequest(token))
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(Unit)
            } else {
                Resource.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            Resource.Error("Gagal register token: ${e.localizedMessage}")
        }
    }

    override suspend fun refreshProfile(): Resource<Unit> {
        if (!networkMonitor.isOnline()) return Resource.Error("Offline")

        return try {
            val response = profileApi.getProfile()
            if (response.isSuccessful && response.body()?.success == true) {
                profileDao.insertProfile(response.body()!!.data!!.toEntity())
                Resource.Success(Unit)
            } else {
                Resource.Error("Gagal refresh")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Unknown error")
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