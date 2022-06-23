package com.meltingb.medicare.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.meltingb.medicare.data.PillEntity
import com.meltingb.medicare.db.dao.PillDao


@Database(
    version = 1, exportSchema = false,
    entities = [
        PillEntity::class
    ],
    )
@TypeConverters(Converters::class)
abstract class RoomDataBase : RoomDatabase() {

    abstract fun pillDao(): PillDao

    companion object {
        private var INSTANCE: RoomDataBase? = null

        fun getDatabase(context: Context, dbName: String): RoomDataBase {
            if (INSTANCE == null) {
                synchronized(RoomDataBase::class) {
                    // db 생성
                    INSTANCE = initDB(context, dbName)
                }
            }
            return INSTANCE!!
        }

        private fun initDB(context: Context, dbName: String): RoomDataBase {
            return Room.databaseBuilder(
                context,
                RoomDataBase::class.java,
                dbName
            )
                .setJournalMode(JournalMode.TRUNCATE)
                .build()
        }
    }
}
