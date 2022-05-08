package com.example.redditdownloader.viewmodel

import android.app.Application
import androidx.annotation.NonNull
import androidx.lifecycle.AndroidViewModel
import com.example.redditdownloader.RedditApp
import com.example.redditdownloader.application.datatransferobjects.SubredditDto
import com.example.redditdownloader.application.mappers.SubredditMapper
import com.example.redditdownloader.core.dao.SubredditDao
import com.example.redditdownloader.core.enum.SubredditType
import java.lang.Exception
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class AddSubredditViewModel : AndroidViewModel {

    val subredditDto = SubredditDto(0, "", SubredditType.SUBREDDIT)
    var subredditDao: SubredditDao

    constructor(@NonNull application: Application) : super(application) {
        subredditDao = RedditApp.getDatabase(application.applicationContext)
                .getSubredditDao()
    }

    fun setName(name: String) {
        subredditDto.name = name
    }

    fun setType(type: SubredditType) {
        subredditDto.type = type
    }

    fun save() {
        // TODO: WTF HOW TO MAKE THIS SHIT WAIT???????
        var existing = subredditDao.getSubredditByName(subredditDto.name)

        if(existing != null) throw Exception("A subreddit with the same name and type already exists")

        subredditDao.insert(SubredditMapper.ToDb(subredditDto)) }

}