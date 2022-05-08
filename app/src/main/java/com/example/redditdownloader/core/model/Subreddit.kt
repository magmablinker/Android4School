package com.example.redditdownloader.core.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.redditdownloader.core.enum.SubredditType

@Entity(tableName = "Subreddit")
data class DbSubreddit(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int,
    @ColumnInfo(name = "name")
    var name: String,
    @ColumnInfo(name = "type")
    var type: SubredditType
) {
    constructor() : this(0, "", SubredditType.SUBREDDIT) {}
}