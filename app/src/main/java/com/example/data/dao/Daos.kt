package com.example.data.dao

import androidx.room.*
import com.example.data.model.Paper
import com.example.data.model.Workspace
import com.example.data.model.FundingOpportunity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaperDao {
    @Query("SELECT * FROM papers ORDER BY creationTime DESC")
    fun getAllPapers(): Flow<List<Paper>>

    @Query("SELECT * FROM papers WHERE id = :id LIMIT 1")
    fun getPaperById(id: Long): Flow<Paper?>

    @Query("SELECT * FROM papers WHERE workspaceId = :workspaceId ORDER BY creationTime DESC")
    fun getPapersByWorkspace(workspaceId: Long): Flow<List<Paper>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaper(paper: Paper): Long

    @Update
    suspend fun updatePaper(paper: Paper)

    @Delete
    suspend fun deletePaper(paper: Paper)

    @Query("DELETE FROM papers WHERE id = :id")
    suspend fun deletePaperById(id: Long)
}

@Dao
interface WorkspaceDao {
    @Query("SELECT * FROM workspaces ORDER BY id ASC")
    fun getAllWorkspaces(): Flow<List<Workspace>>

    @Query("SELECT * FROM workspaces WHERE id = :id LIMIT 1")
    suspend fun getWorkspaceById(id: Long): Workspace?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkspace(workspace: Workspace): Long

    @Delete
    suspend fun deleteWorkspace(workspace: Workspace)
}

@Dao
interface FundingDao {
    @Query("SELECT * FROM funding_opportunities ORDER BY id DESC")
    fun getAllFunding(): Flow<List<FundingOpportunity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFunding(funding: FundingOpportunity): Long

    @Query("DELETE FROM funding_opportunities")
    suspend fun deleteAllFunding()
}
