package com.example.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CassandraLog::class, IpfsSnapshot::class], version = 1, exportSchema = false)
abstract class MemoryDatabase : RoomDatabase() {
    abstract fun cassandraLogDao(): CassandraLogDao
    abstract fun ipfsSnapshotDao(): IpfsSnapshotDao

    companion object {
        @Volatile
        private var INSTANCE: MemoryDatabase? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                synchronized(this) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            MemoryDatabase::class.java,
                            "socxima_memory_db"
                        ).fallbackToDestructiveMigration().build()
                    }
                }
            }
        }

        fun getInstance(): MemoryDatabase {
            return INSTANCE ?: throw IllegalStateException("Database not initialized. Please call initialize(Context) first.")
        }
    }
}
