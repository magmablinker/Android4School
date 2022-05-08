  package com.example.redditdownloader.application.mappers

import com.example.redditdownloader.application.datatransferobjects.SubredditDto
import com.example.redditdownloader.core.model.DbSubreddit
import java.util.*

class SubredditMapper {

    companion object {
        fun ToDb(subredditDto: SubredditDto) : DbSubreddit {
            return DbSubreddit(
                subredditDto.id,
                subredditDto.name,
                subredditDto.type
            );
        }

        fun ToDto(subreddit: DbSubreddit) : SubredditDto {
            return SubredditDto(
                subreddit.id,
                subreddit.name,
                subreddit.type
            );
        }

        fun ToDto(dbSubreddits: List<DbSubreddit>) : List<SubredditDto> {
            return dbSubreddits.map { dbSubreddit -> ToDto(dbSubreddit) }
        }
    }
}