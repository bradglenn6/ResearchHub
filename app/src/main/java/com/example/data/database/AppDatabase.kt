package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.PaperDao
import com.example.data.dao.WorkspaceDao
import com.example.data.dao.FundingDao
import com.example.data.model.Paper
import com.example.data.model.Workspace
import com.example.data.model.FundingOpportunity

@Database(
    entities = [Paper::class, Workspace::class, FundingOpportunity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun paperDao(): PaperDao
    abstract fun workspaceDao(): WorkspaceDao
    abstract fun fundingDao(): FundingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "research_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
