package com.example.db

import kotlinx.coroutines.flow.Flow

class MemoryRepository(private val database: MemoryDatabase) {

    val allCassandraLogs: Flow<List<CassandraLog>> = database.cassandraLogDao().getAllLogs()
    val allIpfsSnapshots: Flow<List<IpfsSnapshot>> = database.ipfsSnapshotDao().getAllSnapshots()

    suspend fun insertCassandraLog(log: CassandraLog) {
        database.cassandraLogDao().insertLog(log)
    }

    suspend fun clearCassandraLogs() {
        database.cassandraLogDao().clearAll()
    }

    suspend fun insertIpfsSnapshot(snapshot: IpfsSnapshot) {
        database.ipfsSnapshotDao().insertSnapshot(snapshot)
    }

    suspend fun deleteIpfsSnapshot(cid: String) {
        database.ipfsSnapshotDao().deleteSnapshot(cid)
    }
}
