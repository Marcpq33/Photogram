package com.photogram.core.database.di

import android.content.Context
import androidx.room.Room
import com.photogram.core.database.PhotogramDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): PhotogramDatabase = Room.databaseBuilder(
        context,
        PhotogramDatabase::class.java,
        PhotogramDatabase.DATABASE_NAME,
    ).build()
}
