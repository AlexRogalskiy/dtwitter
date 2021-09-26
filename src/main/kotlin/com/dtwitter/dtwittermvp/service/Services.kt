package com.dtwitter.dtwittermvp.service

import com.dtwitter.dtwittermvp.model.Channel
import com.dtwitter.dtwittermvp.model.ChannelPath
import com.dtwitter.dtwittermvp.model.Post
import com.dtwitter.dtwittermvp.model.RATE
import com.dtwitter.dtwittermvp.model.RankedPost
import com.dtwitter.dtwittermvp.model.User
import java.util.*

class MainService {
    val users: MutableList<User> = mutableListOf()
    val channels: MutableList<Channel> = mutableListOf()
    val channelRankedPosts: MutableMap<ChannelPath, MutableList<RankedPost>> = mutableMapOf()
    val channelPosts: MutableMap<ChannelPath, MutableList<Post>> = mutableMapOf()
    val userChannelAlreadyReadPosts: MutableMap<UUID, MutableMap<ChannelPath, MutableList<UUID>>> = mutableMapOf()

    fun createChannel(name: String, parent: ChannelPath = ""): Channel {
        val channel = Channel(id = "$parent/$name")
        channels.add(channel)
        channelPosts[channel.id] = mutableListOf()
        channelRankedPosts[channel.id] = mutableListOf()
        return channel
    }

    fun createUser(username: String = "test-init-username"): User {
        val user = User(username = username)
        users.add(user)
        userChannelAlreadyReadPosts[user.id] = mutableMapOf()
        return user
    }

    fun pickChannels(userId: UUID, channelsIds: List<ChannelPath>) {
        val user = users.find { it.id == userId }!!
        channelsIds.forEach {
            val channel = channels.find { c -> c.id == it }!!
            channel.peopleNumber++
            user.readingChannels.add(it)
            userChannelAlreadyReadPosts[userId]!![it] = mutableListOf()
        }
    }

    fun unpickChannels(userId: UUID, channelsIds: List<ChannelPath>) {
        val user = users.find { it.id == userId }!!
        channelsIds.forEach {
            user.readingChannels.remove(it)
        }
    }

    val RECOMMENDED_MESSAGES_LIMIT = 10
    fun getRelevantChannelMessages(userId: UUID, channelId: ChannelPath): List<Post> {
        val alreadyReadMessages = userChannelAlreadyReadPosts[userId]?.get(channelId) ?: listOf()
        val messages = channelRankedPosts[channelId]?.filter { it.alreadySeeCount < it.toSeeCount }?.sortedBy { it.thumbUp - it.thumbDown }?.filter { it.id !in alreadyReadMessages } ?: listOf()
        messages.forEach {
            it.alreadySeeCount++
        }
        val postIds = messages.map { it.postId }
        return channelPosts[channelId]!!.filter { it.id in postIds }
    }

    fun writePost(userId: UUID, channelId: ChannelPath, text: String) {
        val post = Post(userId, text, channelId)
        val rankedPost = buildRankedPost(post)
        channelPosts[channelId]!!.add(post)
        channelRankedPosts[channelId]!!.add(rankedPost)
        userChannelAlreadyReadPosts[userId]!![channelId]!!.add(rankedPost.id)
    }

    private fun buildRankedPost(post: Post): RankedPost {
        val user = users.find { it.id == post.userId }!!
        val channelRate = user.channelRates.computeIfAbsent(post.channelId) { 1.0 }
        val channel = channels.find { it.id == post.channelId }!!
        val toSeeCount = (channel.peopleNumber * channelRate).toInt()
        return RankedPost(post.id, toSeeCount, post.timestamp)
    }

    fun ratePost(userId: UUID, channelId: ChannelPath, postId: UUID, rate: RATE) {
        val post = channelRankedPosts[channelId]?.find { it.postId == postId } ?: throw IllegalArgumentException("Cannot find post.")
        post.alreadySeeCount++
        if (rate == RATE.UP) {
            post.thumbUp++
        } else {
            post.thumbDown++
        }
        markAsRead(userId, channelId, postId)
    }

    fun markAsRead(userId: UUID, channelId: ChannelPath, postId: UUID) {
        val userChannelMap = userChannelAlreadyReadPosts[userId]!!
        val channelReadPosts = userChannelMap[channelId]!!
        channelReadPosts.add(postId)
    }

    fun rerank() {
        channelRankedPosts.values.forEach {
            it.filter { it.alreadySeeCount >= it.toSeeCount }
                .filter {
                    it.thumbUp - it.thumbDown > 0
                }
                .forEach {
                    it.toSeeCount += 1
                }
        }
    }
}


// - create user
// - pick channels
// - open channel == get relevant channel messages
// - read posts

// - rate posts
// change post rate
// scroll down

// write post
// propagate new post



// 10% of channel -> 10 picks +-
// timestamp -> new at first
// likes vs dislikes ->