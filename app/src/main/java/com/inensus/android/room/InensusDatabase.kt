package com.inensus.android.room

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.inensus.android.model.Customer


@Database(entities = [(Customer::class)], version = 3)
abstract class InensusDatabase : RoomDatabase() {

    abstract fun customerDao(): CustomerDao

    companion object {
        private var INSTANCE: InensusDatabase? = null

        internal fun getDatabase(context: Context): InensusDatabase? {
            if (INSTANCE == null) {
                synchronized(InensusDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(context.applicationContext,
                                InensusDatabase::class.java, "inensus_database")
                                .fallbackToDestructiveMigration()
                                .build()
                    }
                }
            }
            return INSTANCE
        }
    }
}
