package com.dtwitter.dtwittermvp.model.request

import com.dtwitter.dtwittermvp.model.ChannelPath
import com.dtwitter.dtwittermvp.model.mongo.PostContent
import java.util.*

data class CreateChannelRequest(
    val userId: UUID,
    val channelName: String,
    val parentChannelPath: ChannelPath
)

data class CreateUserRequest(
    val username: String
)

data class ChangeUserChannelsRequest(
    val userId: UUID,
    val channelIds: Set<ChannelPath>
)

data class CreatePostRequest(
    val userId: UUID,
    val channelPath: ChannelPath,
    val content: PostContent
)