package com.example.redditdownloader

import android.content.Context
import androidx.room.Room
import com.example.redditdownloader.core.database.RedditDownloaderDatabase

class RedditApp {

    companion object {
        private var database: RedditDownloaderDatabase? = null

        fun getDatabase(context: Context) : RedditDownloaderDatabase {

            if(database == null)
                database = Room.databaseBuilder(context, RedditDownloaderDatabase::class.java, "RedditDownloaderDatabase")
                        .fallbackToDestructiveMigration()
                        .build()

            return database!!
        }
    }

}