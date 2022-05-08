package com.example.redditdownloader.viewmodel

import android.app.Application
import androidx.annotation.NonNull
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.redditdownloader.RedditApp
import com.example.redditdownloader.application.datatransferobjects.SubredditDto
import com.example.redditdownloader.application.mappers.SubredditMapper
import com.example.redditdownloader.core.dao.SubredditDao
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class MainActivityViewModel : AndroidViewModel {

    var subreddits : MutableLiveData<MutableList<SubredditDto>> = MutableLiveData()
    var subredditDao: SubredditDao
    var executor: Executor = Executors.newSingleThreadExecutor()
    var amountPosts = 25
    var postType = "hot"
    var isDownloading = MutableLiveData<Boolean>(false)

    constructor(@NonNull application: Application) : super(application) {
        subredditDao = RedditApp.getDatabase(application.applicationContext)
            .getSubredditDao()

        executor.execute {
            subreddits.postValue(SubredditMapper.ToDto(subredditDao.getSubreddits()).toMutableList())
        }
    }

    fun removeSubreddit(subredditDto: SubredditDto) {
        subredditDao.delete(SubredditMapper.ToDb(subredditDto))

        val value = subreddits.value ?: return

        value.remove(subredditDto)

        subreddits.postValue(value)
    }

    fun setDownloading(value: Boolean) {
        isDownloading.postValue(value)
    }

}