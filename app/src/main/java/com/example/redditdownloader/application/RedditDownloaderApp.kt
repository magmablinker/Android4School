package com.example.redditdownloader.application

import android.app.Application
import androidx.room.Room
import com.example.redditdownloader.core.database.RedditDownloaderDatabase

class RedditDownloaderApp : Application() {

    companion object {
        lateinit var redditDownloaderDb: RedditDownloaderDatabase
    }

    override fun onCreate() {
        super.onCreate()

        redditDownloaderDb = Room.databaseBuilder(
            applicationContext,
            RedditDownloaderDatabase::class.java,
            "RedditDownloader-Db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

}