package com.dtwitter.dtwittermvp.model

import java.sql.Timestamp
import java.time.Instant
import java.util.*

data class User(
    val id: UUID = UUID.randomUUID(),
    val username: String,
    val ownChannels: MutableSet<ChannelPath> = mutableSetOf(),
    val readingChannels: MutableSet<ChannelPath> = mutableSetOf(),

    val channelRates: MutableMap<ChannelPath, Double> = mutableMapOf(), // rate from 0 to 1
    val avgRate: Double = 0.0
)

data class UserRecommendMessages(
    val id: UUID = UUID.randomUUID(),
    val userId: UUID,
    val channelId: UUID,
    val posts: MutableList<Post> = mutableListOf()
)

data class Channel(
    val id: ChannelPath,
    var peopleNumber: Int = 0,
    val subChannels: MutableList<UUID> = mutableListOf(),
    val status: ChannelStatus = ChannelStatus.PENDING
)

typealias ChannelPath = String

data class Post(
    val userId: UUID,
    val text: String,
    val channelId: ChannelPath,
    val id: UUID = UUID.randomUUID(),
    val timestamp: Timestamp = Timestamp.from(Instant.now()),
)

data class RankedPost(
    val postId: UUID,
    var toSeeCount: Int,
    val timestamp: Timestamp,
    val id: UUID = UUID.randomUUID(),
    var alreadySeeCount: Int = 0,
    var thumbUp: Int = 0,
    var thumbDown: Int = 0,
)

data class UserChannelMessage(
    val id: UUID,
    val messageId: UUID,
    val status: UserMessageStatus = UserMessageStatus.NOT_READ
)

enum class ChannelStatus {
    PENDING,
    CREATED,
    DELETED
}

enum class UserMessageStatus {
    READ,
    NOT_READ
}

enum class RATE {
    UP,
    DOWN
}