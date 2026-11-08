package com.ejemplo.trace.di

import android.content.Context
import androidx.room.Room
import com.ejemplo.trace.data.local.TraceDatabase
import com.ejemplo.trace.data.local.dao.ProductHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt para proveer la base de datos Room y sus DAOs.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideTraceDatabase(
        @ApplicationContext context: Context
    ): TraceDatabase {
        return Room.databaseBuilder(
            context,
            TraceDatabase::class.java,
            TraceDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideProductHistoryDao(database: TraceDatabase): ProductHistoryDao {
        return database.productHistoryDao()
    }
}
