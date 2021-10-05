package com.dtwitter.dtwittermvp.model.mongo

import com.dtwitter.dtwittermvp.model.ChannelPath
import com.dtwitter.dtwittermvp.model.ChannelStatus
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.sql.Timestamp
import java.time.Instant
import java.util.*


@Document(collection = "user")
data class User(
    @Id
    val id: UUID = UUID.randomUUID(),
    val username: String,
    val readingChannels: Set<ChannelPath> = setOf(),
    val channelRates: Map<ChannelPath, Double> = mapOf(), // rate from 0 to 1
    val avgRate: Double = 0.0
)

@Document(collation = "user_read_post")
data class UserReadPost(
    @Id
    val id: UserReadPostId,
    val timestamp: Instant
)

data class UserReadPostId(
    val userId: UUID,
    val channelId: ChannelPath,
    val postId: UUID,
)

/////////////////////////////////////////////////////////////////////////////////////////////

@Document(collection = "channel")
data class Channel(
    @Id
    val id: ChannelPath,
    val owners: List<UUID> = listOf(), // list of user ids
    val peopleNumber: Int = 1,
    val subChannels: List<UUID> = listOf(),
    val status: ChannelStatus = ChannelStatus.PENDING
)

/////////////////////////////////////////////////////////////////////////////////////////////

@Document(collection = "post")
data class Post(
    @Id
    val id: PostId,
    val toSeeCount: Int,

    val timestamp: Instant = Instant.now(),
    val alreadySeeCount: Int = 0,

    val thumbUp: Int = 0,
    val thumbDown: Int = 0,

    val postContent: PostContent
)

data class PostId(
    val userId: UUID,
    val channelId: ChannelPath,
    val postId: UUID,
)

data class PostContent(
    val text: String
)