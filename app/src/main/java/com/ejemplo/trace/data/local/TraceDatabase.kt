package com.ejemplo.trace.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ejemplo.trace.data.local.dao.ProductHistoryDao
import com.ejemplo.trace.data.local.entity.ScannedProductEntity

/**
 * Base de datos Room para la persistencia local de TRACE.
 * Solo almacena historial y caché efímero con TTL de los productos escaneados por el usuario.
 */
@Database(
    entities = [ScannedProductEntity::class],
    version = 1,
    exportSchema = false
)
abstract class TraceDatabase : RoomDatabase() {

    abstract fun productHistoryDao(): ProductHistoryDao

    companion object {
        const val DATABASE_NAME = "trace_local_cache.db"
    }
}
