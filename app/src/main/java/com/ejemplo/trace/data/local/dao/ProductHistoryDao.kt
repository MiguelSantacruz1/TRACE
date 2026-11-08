package com.ejemplo.trace.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ejemplo.trace.data.local.entity.ScannedProductEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para el historial de escaneos y caché local efímero.
 */
@Dao
interface ProductHistoryDao {

    @Query("SELECT * FROM scanned_products ORDER BY scannedAt DESC")
    fun getAllHistory(): Flow<List<ScannedProductEntity>>

    @Query("SELECT * FROM scanned_products WHERE barcode = :barcode LIMIT 1")
    suspend fun getProductByBarcode(barcode: String): ScannedProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entity: ScannedProductEntity)

    @Query("DELETE FROM scanned_products WHERE barcode = :barcode")
    suspend fun deleteByBarcode(barcode: String)

    @Query("DELETE FROM scanned_products")
    suspend fun clearAll()

    @Query("DELETE FROM scanned_products WHERE scannedAt + cacheTtlMillis < :now")
    suspend fun purgeExpiredCache(now: Long = System.currentTimeMillis())
}
