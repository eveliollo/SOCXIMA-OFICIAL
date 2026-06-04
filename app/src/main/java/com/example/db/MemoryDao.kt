package com.example.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CassandraLogDao {
    @Query("SELECT * FROM cassandra_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<CassandraLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: CassandraLog)

    @Query("DELETE FROM cassandra_logs")
    suspend fun clearAll()
}

@Dao
interface IpfsSnapshotDao {
    @Query("SELECT * FROM ipfs_snapshots ORDER BY timestamp DESC")
    fun getAllSnapshots(): Flow<List<IpfsSnapshot>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSnapshot(snapshot: IpfsSnapshot)

    @Query("DELETE FROM ipfs_snapshots WHERE cid = :cid")
    suspend fun deleteSnapshot(cid: String)
}
