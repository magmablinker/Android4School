package com.example.redditdownloader.core.dao

import androidx.room.*
import com.example.redditdownloader.core.enum.SubredditType
import com.example.redditdownloader.core.model.DbSubreddit
import java.util.*

@Dao
abstract class SubredditDao {

    @Transaction
    @Query("SELECT * FROM subreddit")
    abstract fun getSubreddits() : List<DbSubreddit>

    @Insert
    abstract fun insert(subreddit: DbSubreddit)

    @Update
    abstract fun update(subreddit: DbSubreddit)

    @Delete
    abstract fun delete(subreddit: DbSubreddit)

    @Transaction
    @Query("SELECT * FROM subreddit WHERE name = :name")
    abstract fun getSubredditByName(name: String) : DbSubreddit

}