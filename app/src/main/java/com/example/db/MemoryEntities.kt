package com.example.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cassandra_logs")
data class CassandraLog(
    @PrimaryKey val id: String, // Represent UUID as standard String format
    val timestamp: Long,       // Unix Epoch Miliseconds
    val totalValue: Long,      // BigInt equivalent in Kotlin
    val currentFloor: Long,    // BigInt equivalent in Kotlin
    val comments: String       // Extra metadata
)

@Entity(tableName = "ipfs_snapshots")
data class IpfsSnapshot(
    @PrimaryKey val cid: String, // IPFS Unique CID Hash address e.g. Qm...
    val timestamp: Long,
    val contentJson: String,    // Content payload of the system snapshot
    val sizeBytes: Long,
    val comments: String
)

data class Libp2pPeer(
    val nodeId: String,
    val address: String,
    val latencyMs: Long,
    val isConnected: Boolean,
    val connectedSince: String
)
