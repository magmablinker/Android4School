package com.example.redditdownloader.application.datatransferobjects

import com.example.redditdownloader.core.enum.SubredditType

data class SubredditDto(
    var id: Int,
    var name: String,
    var type: SubredditType
) {
}