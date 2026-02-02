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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
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
                            coroutineScope {
                                // 1. Trigger network refresh in background
                                launch {
                                    if (networkMonitor.isOnline()) {
                                        try {
                                            val response = plafondApi.getActivePlafond()
                                            if (response.isSuccessful) {
                                                val body = response.body()
                                                if (body?.success == true) {
                                                    val plafondDto = body.data
                                                    if (plafondDto != null) {
                                                        // Check if plafond is actually active and has remaining amount
                                                        if (plafondDto.isActive && plafondDto.remainingAmount > 0) {
                                                            plafondDao.insertPlafond(plafondDto.toEntity(userId))
                                                        } else {
                                                            plafondDao.deletePlafondByUserId(userId)
                                                        }
                                                    } else {
                                                        plafondDao.deletePlafondByUserId(userId)
                                                    }
                                                } else {
                                                    // Logical error (e.g. "No active plafond"), clear cache
                                                    plafondDao.deletePlafondByUserId(userId)
                                                }
                                            } else if (response.code() == 404) {
                                                // Resource not found, clear cache
                                                plafondDao.deletePlafondByUserId(userId)
                                            }
                                        } catch (e: Exception) {
                                            // Ignore network errors, rely on cache
                                        }
                                    }
                                }

                                // 2. Observe Database (Source of Truth)
                                // This will run indefinitely until the flow is cancelled
                                plafondDao.getActivePlafondByUserId(userId).collect { entity ->
                                    if (entity != null) {
                                        emit(Resource.Success(entity.toDomain()))
                                    } else {
                                        emit(Resource.Success(null)) // Valid state: User has no active plafond
                                    }
                                }
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

        val userId = tokenManager.getUserId().first() ?: return Resource.Error("User Session Expired")

        return try {
            val response = plafondApi.selectPlafond(SelectPlafondRequest(productId))

            if (response.isSuccessful && response.body()?.success == true) {
                val plafondDto = response.body()!!.data!!
                plafondDao.insertPlafond(plafondDto.toEntity(userId))
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
        
        val userId = tokenManager.getUserId().first() ?: return Resource.Error("User Session Expired")

        return try {
            val response = plafondApi.getActivePlafond()
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true) {
                    val data = body.data
                    if (data != null) {
                        if (data.isActive && data.remainingAmount > 0) {
                            plafondDao.insertPlafond(data.toEntity(userId))
                        } else {
                            plafondDao.deletePlafondByUserId(userId)
                        }
                    } else {
                        plafondDao.deletePlafondByUserId(userId)
                    }
                    Resource.Success(Unit)
                } else {
                    // Logical error (e.g. "No active plafond"), clear cache
                    plafondDao.deletePlafondByUserId(userId)
                    Resource.Error(body?.message ?: "Gagal refresh")
                }
            } else {
                if (response.code() == 404) {
                     plafondDao.deletePlafondByUserId(userId)
                }
                Resource.Error("Gagal refresh: ${response.message()}")
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