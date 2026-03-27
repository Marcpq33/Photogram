package com.photogram.core.database

import androidx.room.Database
import androidx.room.RoomDatabase

// PlaceholderEntity is temporary — replaced when real entities are mapped
@Database(
    entities = [PlaceholderEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class PhotogramDatabase : RoomDatabase() {

    companion object {
        const val DATABASE_NAME = "photogram.db"
    }
}
