package com.telesoftas.justasonboardingapp.utils.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        ArticleEntity::class,
        SourceEntity::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
    abstract fun newsSourceDao(): SourceDao
}
