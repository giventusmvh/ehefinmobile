package com.example.ehefin_mobile.feature.plafond.data.source.local

import com.example.ehefin_mobile.core.common.DataResult
import com.example.ehefin_mobile.core.datastore.TokenManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 * Implementation of PlafondLocalDataSource.
 * Wraps Room DAO calls with proper error handling and returns DataResult.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PlafondLocalDataSourceImpl @Inject constructor(
    private val plafondDao: PlafondDao,
    private val productDao: ProductDao,
    private val tokenManager: TokenManager
) : PlafondLocalDataSource {

    override fun getActivePlafondFlow(): Flow<PlafondEntity?> {
        return tokenManager.getUserId().flatMapLatest { userId ->
            if (userId != null) {
                plafondDao.getActivePlafondByUserId(userId)
            } else {
                flowOf(null)
            }
        }
    }

    override suspend fun getActivePlafond(): DataResult<PlafondEntity?> {
        return try {
            val userId = tokenManager.getUserId().first()
            if (userId != null) {
                val plafond = plafondDao.getActivePlafondByUserIdSync(userId)
                DataResult.Success(plafond)
            } else {
                DataResult.Error("User ID tidak ditemukan")
            }
        } catch (e: Exception) {
            DataResult.Error(
                message = e.localizedMessage ?: "Gagal mengambil data plafond",
                throwable = e
            )
        }
    }

    override suspend fun savePlafond(plafond: PlafondEntity): DataResult<Unit> {
        return try {
            plafondDao.insertPlafond(plafond)
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(
                message = e.localizedMessage ?: "Gagal menyimpan data plafond",
                throwable = e
            )
        }
    }

    override suspend fun clearPlafond(): DataResult<Unit> {
        return try {
            val userId = tokenManager.getUserId().first()
            if (userId != null) {
                plafondDao.deletePlafondByUserId(userId)
            }
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(
                message = e.localizedMessage ?: "Gagal menghapus data plafond",
                throwable = e
            )
        }
    }

    override fun getAllProductsFlow(): Flow<List<ProductEntity>> {
        return productDao.getAllProducts()
    }

    override suspend fun saveProducts(products: List<ProductEntity>): DataResult<Unit> {
        return try {
            productDao.deleteAllAndInsert(products)
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(
                message = e.localizedMessage ?: "Gagal menyimpan data produk",
                throwable = e
            )
        }
    }
}