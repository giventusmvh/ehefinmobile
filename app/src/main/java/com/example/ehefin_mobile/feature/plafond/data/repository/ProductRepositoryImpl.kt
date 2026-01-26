package com.example.ehefin_mobile.feature.plafond.data.repository

import com.example.ehefin_mobile.core.common.Resource
import com.example.ehefin_mobile.core.di.IoDispatcher
import com.example.ehefin_mobile.core.network.NetworkMonitor
import com.example.ehefin_mobile.feature.plafond.data.mapper.toDomain
import com.example.ehefin_mobile.feature.plafond.data.mapper.toEntity
import com.example.ehefin_mobile.feature.plafond.data.source.local.ProductDao
import com.example.ehefin_mobile.feature.plafond.data.source.remote.PlafondApi
import com.example.ehefin_mobile.feature.plafond.domain.model.Product
import com.example.ehefin_mobile.feature.plafond.domain.repository.ProductRepository
import com.google.gson.Gson
import com.google.gson.JsonObject
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

class ProductRepositoryImpl
@Inject
constructor(
        private val plafondApi: PlafondApi,
        private val productDao: ProductDao,
        private val networkMonitor: NetworkMonitor,
        private val gson: Gson,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ProductRepository {

    override fun getProducts(): Flow<Resource<List<Product>>> =
            flow {
                        emit(Resource.Loading())

                        // 1. Emit cached data first
                        val cachedProducts = productDao.getAllProducts().first()
                        if (cachedProducts.isNotEmpty()) {
                            emit(Resource.Success(cachedProducts.map { it.toDomain() }))
                        }

                        // 2. Fetch from network if online
                        if (networkMonitor.isOnline()) {
                            try {
                                val response = plafondApi.getProducts()
                                if (response.isSuccessful && response.body()?.success == true) {
                                    val products = response.body()!!.data ?: emptyList()
                                    // Update cache
                                    productDao.deleteAllAndInsert(products.map { it.toEntity() })
                                    emit(Resource.Success(products.map { it.toDomain() }))
                                } else {
                                    val error = parseErrorMessage(response)
                                    if (cachedProducts.isNotEmpty()) {
                                        emit(
                                                Resource.Error(
                                                        error,
                                                        cachedProducts.map { it.toDomain() }
                                                )
                                        )
                                    } else {
                                        emit(Resource.Error(error))
                                    }
                                }
                            } catch (e: Exception) {
                                val error = "Gagal memuat produk: ${e.localizedMessage}"
                                if (cachedProducts.isNotEmpty()) {
                                    emit(
                                            Resource.Error(
                                                    error,
                                                    cachedProducts.map { it.toDomain() }
                                            )
                                    )
                                } else {
                                    emit(Resource.Error(error))
                                }
                            }
                        } else if (cachedProducts.isEmpty()) {
                            emit(Resource.Error("Tidak ada koneksi internet"))
                        }
                    }
                    .flowOn(ioDispatcher)

    override suspend fun refreshProducts(): Resource<Unit> {
        if (!networkMonitor.isOnline()) return Resource.Error("Offline")

        return try {
            val response = plafondApi.getProducts()
            if (response.isSuccessful && response.body()?.success == true) {
                val products = response.body()!!.data ?: emptyList()
                productDao.deleteAllAndInsert(products.map { it.toEntity() })
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
