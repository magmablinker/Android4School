package com.example.redditdownloader.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.redditdownloader.core.dao.SubredditDao
import com.example.redditdownloader.core.model.DbSubreddit

@Database(entities = [DbSubreddit::class], version = 5)
abstract class RedditDownloaderDatabase : RoomDatabase() {

   abstract fun getSubredditDao() : SubredditDao

}