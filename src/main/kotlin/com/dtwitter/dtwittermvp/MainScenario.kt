package com.dtwitter.dtwittermvp

import com.dtwitter.dtwittermvp.model.POST_RATE
import com.dtwitter.dtwittermvp.service.MainService

fun main() {
    val mainService = MainService()
    val channel = mainService.createChannel("sport")
    val user = mainService.createUser()
    mainService.pickChannels(user.id, listOf(channel.id))
    mainService.writePost(user.id, channel.id, "test text")

    val recommendedPosts = mainService.getRelevantChannelMessages(user.id, channel.id)
    println(recommendedPosts)
    mainService.ratePost(user.id, channel.id, recommendedPosts[0].id, POST_RATE.UP)
}

object Test1 {
    @JvmStatic
    fun main(args: Array<String>) {
        val mainService = MainService()
        val channel = mainService.createChannel("sport")
        val user = mainService.createUser("user1")
        mainService.pickChannels(user.id, listOf(channel.id))
        mainService.writePost(user.id, channel.id, "test text 1")

        val user2 = mainService.createUser("user2")
        mainService.pickChannels(user2.id, listOf(channel.id))
        mainService.writePost(user2.id, channel.id, "test text 2")

        println(mainService.channelRankedPosts)

        val recommendedPosts = mainService.getRelevantChannelMessages(user.id, channel.id)
        assert(recommendedPosts.size == 1)
        mainService.ratePost(user.id, channel.id, recommendedPosts[0].id, POST_RATE.UP)
        println(mainService.channelRankedPosts)

        val recommendedPosts2 = mainService.getRelevantChannelMessages(user2.id, channel.id)
        assert(recommendedPosts2.size == 1)
        mainService.ratePost(user2.id, channel.id, recommendedPosts2[0].id, POST_RATE.DOWN)
        println(mainService.channelRankedPosts)

        mainService.rerank()
        println(mainService.channelRankedPosts)
    }
}
