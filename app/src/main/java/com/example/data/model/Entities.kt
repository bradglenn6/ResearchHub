package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "papers")
data class Paper(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val authors: String,
    val journal: String,
    val year: String,
    val url: String = "",
    val doi: String = "",
    val textContent: String = "",
    val category: String = "",
    
    // AI analysis fields
    val summary: String? = null,
    val keyFindings: String? = null,  // Bullet points
    val suggestedSources: String? = null, // Suggested peer-reviewed references
    val plagiarismScore: Int? = null, // % (0-100)
    val plagiarismReport: String? = null,
    
    // Automated Citations
    val citationApa: String? = null,
    val citationMla: String? = null,
    val citationChicago: String? = null,
    
    val workspaceId: Long = 1, // Default to Personal Workspace (id = 1)
    val savedOffline: Boolean = true,
    val isPinned: Boolean = false,
    val creationTime: Long = System.currentTimeMillis()
)

@Entity(tableName = "workspaces")
data class Workspace(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val owner: String,
    val teamMembers: String // Comma separated values e.g., " Brad G, Sarah L"
)

@Entity(tableName = "funding_opportunities")
data class FundingOpportunity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val agency: String,
    val amount: String,
    val description: String,
    val deadline: String,
    val matchingTopic: String
)
