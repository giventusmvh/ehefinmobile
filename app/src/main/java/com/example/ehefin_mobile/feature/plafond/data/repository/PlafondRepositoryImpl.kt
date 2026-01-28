package com.example.ehefin_mobile.feature.plafond.data.repository

import com.example.ehefin_mobile.core.common.Resource
import com.example.ehefin_mobile.core.datastore.TokenManager
import com.example.ehefin_mobile.core.di.IoDispatcher
import com.example.ehefin_mobile.core.network.NetworkMonitor
import com.example.ehefin_mobile.feature.plafond.data.mapper.toDomain
import com.example.ehefin_mobile.feature.plafond.data.mapper.toEntity
import com.example.ehefin_mobile.feature.plafond.data.source.local.PlafondDao
import com.example.ehefin_mobile.feature.plafond.data.source.remote.PlafondApi
import com.example.ehefin_mobile.feature.plafond.data.source.remote.dto.SelectPlafondRequest
import com.example.ehefin_mobile.feature.plafond.domain.model.UserPlafond
import com.example.ehefin_mobile.feature.plafond.domain.repository.PlafondRepository
import com.google.gson.Gson
import com.google.gson.JsonObject
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

class PlafondRepositoryImpl
@Inject
constructor(
        private val plafondApi: PlafondApi,
        private val plafondDao: PlafondDao,
        private val tokenManager: TokenManager,
        private val networkMonitor: NetworkMonitor,
        private val gson: Gson,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : PlafondRepository {

    override fun getActivePlafond(): Flow<Resource<UserPlafond?>> =
            flow<Resource<UserPlafond?>> {
                        emit(Resource.Loading())

                        val userId = tokenManager.getUserId().first()

                        if (userId != null) {
                            // 1. Emit cached data first
                            val cachedPlafond = plafondDao.getActivePlafondByUserIdSync(userId)
                            if (cachedPlafond != null) {
                                emit(Resource.Success(cachedPlafond.toDomain()))
                            }

                            // 2. Fetch from network
                            if (networkMonitor.isOnline()) {
                                try {
                                    val response = plafondApi.getActivePlafond()
                                    if (response.isSuccessful && response.body()?.success == true) {
                                        val plafondDto = response.body()!!.data
                                        if (plafondDto != null) {
                                            plafondDao.insertPlafond(plafondDto.toEntity())
                                            emit(Resource.Success(plafondDto.toDomain()))
                                        } else {
                                            // User doesn't have a plafond yet
                                            emit(Resource.Success(null))
                                        }
                                    } else {
                                        val error = parseErrorMessage(response)
                                        if (cachedPlafond != null) {
                                            emit(Resource.Error(error, cachedPlafond.toDomain()))
                                        } else {
                                            emit(Resource.Error(error))
                                        }
                                    }
                                } catch (e: Exception) {
                                    val error = "Gagal memuat plafond: ${e.localizedMessage}"
                                    if (cachedPlafond != null) {
                                        emit(Resource.Error(error, cachedPlafond.toDomain()))
                                    } else {
                                        emit(Resource.Error(error))
                                    }
                                }
                            } else if (cachedPlafond == null) {
                                emit(Resource.Success(null)) // No cache and offline
                            }
                        } else {
                            emit(Resource.Error("User ID tidak ditemukan"))
                        }
                    }
                    .flowOn(ioDispatcher)

    override suspend fun selectPlafond(productId: Long): Resource<UserPlafond> {
        if (!networkMonitor.isOnline()) {
            return Resource.Error("Tidak ada koneksi internet")
        }

        return try {
            val response = plafondApi.selectPlafond(SelectPlafondRequest(productId))

            if (response.isSuccessful && response.body()?.success == true) {
                val plafondDto = response.body()!!.data!!
                plafondDao.insertPlafond(plafondDto.toEntity())
                Resource.Success(plafondDto.toDomain())
            } else {
                Resource.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            Resource.Error("Gagal memilih plafond: ${e.localizedMessage}")
        }
    }

    override suspend fun refreshPlafond(): Resource<Unit> {
        if (!networkMonitor.isOnline()) return Resource.Error("Offline")

        return try {
            val response = plafondApi.getActivePlafond()
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()!!.data?.let { plafondDao.insertPlafond(it.toEntity()) }
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