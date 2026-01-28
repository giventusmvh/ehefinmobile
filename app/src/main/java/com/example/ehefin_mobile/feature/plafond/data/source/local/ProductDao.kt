package com.example.ehefin_mobile.feature.plafond.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for Product operations
 */
@Dao
interface ProductDao {
    
    @Query("SELECT * FROM products ORDER BY amount ASC")
    fun getAllProducts(): Flow<List<ProductEntity>>
    
    @Query("SELECT * FROM products WHERE id = :productId")
    suspend fun getProductById(productId: Long): ProductEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)
    
    @Query("DELETE FROM products")
    suspend fun deleteAllProducts()
    
    @Transaction
    suspend fun deleteAllAndInsert(products: List<ProductEntity>) {
        deleteAllProducts()
        insertProducts(products)
    }
}