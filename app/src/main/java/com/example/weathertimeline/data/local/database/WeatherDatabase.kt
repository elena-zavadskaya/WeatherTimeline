package com.example.weathertimeline.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weathertimeline.data.local.converters.LocalDateTimeConverter
import com.example.weathertimeline.data.local.dao.WeatherHistoryDao
import com.example.weathertimeline.data.local.entity.WeatherHistoryEntity

@Database(
    entities = [WeatherHistoryEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(LocalDateTimeConverter::class)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun weatherHistoryDao(): WeatherHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: WeatherDatabase? = null

        fun getInstance(context: android.content.Context): WeatherDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDatabase::class.java,
                    "weather_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}