package com.photogram.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey

// TEMPORARY — exists solely to satisfy Room's non-empty entities requirement.
// Remove once real entities are mapped in a later milestone.
@Entity(tableName = "_placeholder")
internal data class PlaceholderEntity(
    @PrimaryKey val id: Int = 0,
)
