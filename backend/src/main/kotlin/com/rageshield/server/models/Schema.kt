package com.rageshield.server.models

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object Reports : Table("reports") {
    val id = integer("id").autoIncrement()
    val platform = varchar("platform", 50)
    val contentId = varchar("content_id", 255)
    val contentHash = varchar("content_hash", 64) // Added for fingerprinting
    val category = varchar("category", 100)
    val reporterId = varchar("reporter_id", 64)
    val status = varchar("status", 20).default("PENDING")
    val createdAt = timestamp("created_at")
    
    override val primaryKey = PrimaryKey(id)
}

object Votes : Table("votes") {
    val id = integer("id").autoIncrement()
    val reportId = integer("report_id") references Reports.id
    val voterId = varchar("voter_id", 64)
    val voteType = integer("vote_type") 
    
    override val primaryKey = PrimaryKey(id)
}
