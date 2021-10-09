package com.dtwitter.dtwittermvp.service

import com.dtwitter.dtwittermvp.model.ChannelPath
import com.dtwitter.dtwittermvp.model.POST_RATE
import com.dtwitter.dtwittermvp.model.kafka.PostRate
import com.dtwitter.dtwittermvp.model.mongo.Channel
import com.dtwitter.dtwittermvp.model.mongo.Post
import com.dtwitter.dtwittermvp.model.mongo.PostContent
import com.dtwitter.dtwittermvp.model.mongo.PostId
import com.dtwitter.dtwittermvp.model.mongo.User
import com.dtwitter.dtwittermvp.queue.PostRateProducer
import com.dtwitter.dtwittermvp.repository.ChannelRepository
import com.dtwitter.dtwittermvp.repository.PostRepository
import com.dtwitter.dtwittermvp.repository.UserReadPostRepository
import com.dtwitter.dtwittermvp.repository.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.util.*
import javax.annotation.PostConstruct
import kotlin.math.max

@Service
class Service(val postRepo: PostRepository,
              val userRepository: UserRepository,
              val channelRepository: ChannelRepository,
              val userReadPostRepository: UserReadPostRepository,
              val postRateProducer: PostRateProducer) {

    @PostConstruct
    fun init() {
        val user1 = createUser("user1")
        val channel = createChannel(user1.id, "sport")
        changeChannels(user1.id, setOf(channel.id))

        val user2 = createUser("user2")
        changeChannels(user2.id, setOf(channel.id))

        writePost(user1.id, channel.id, PostContent("test text"))

        val posts1 = getPosts(user1.id, channel.id)
        println(posts1)

        val posts2 = getPosts(user2.id, channel.id)
        println(posts2)

        println(postRepo.findAll())
        ratePost(user2.id, posts2[0].id, POST_RATE.UP)
        Thread.sleep(3000)
        println("Remove after wait")
        println(postRepo.findAll())

//        postRepo.deleteAll()
  }

    fun createUser(username: String): User {
        val user = User(username = username)
        return userRepository.save(user)
    }

    fun createChannel(userOwnerId: UUID, channelName: String, parent: ChannelPath = ""): Channel {
        val channel = Channel("$parent/$channelName")
        val createdChannel = channelRepository.save(channel)
        val user = userRepository.findById(userOwnerId).orElseThrow { IllegalArgumentException() }
        userRepository.save(
            user.copy(
                channelRates = user.channelRates + (channel.id to 0.0),
                readingChannels = user.readingChannels + channel.id)
        )
        return createdChannel
    }

    fun changeChannels(userId: UUID, channelIds: Set<ChannelPath>) {
        val user = userRepository.findById(userId).orElseThrow { IllegalArgumentException() }
        val existingChannelRates = user.channelRates.filter { it.key in channelIds }
        val newChannelRates = channelIds.filter { existingChannelRates[it] == null }.associateWith { 0.0 }
        userRepository.save(user.copy(readingChannels = channelIds, channelRates = existingChannelRates + newChannelRates))
    }

    fun deleteChannels() {
        channelRepository.deleteAll()
    }

    fun getChannels(): List<Channel> {
        return channelRepository.findAll()
    }

    fun ratePost(userId: UUID, postId: PostId, postRate: POST_RATE) {
        postRateProducer.sendPost(PostRate(userId, postId, postRate))
    }

    fun getPosts(userId: UUID, channelPath: ChannelPath): List<Post> {
        val readPosts = userReadPostRepository.findByIdUserIdAndIdChannelId(userId, channelPath)
        val posts = postRepo.findPostToRead(userId, channelPath, readPosts.map { it.id.postId }, PageRequest.of(0, 5))
        return postRepo.saveAll(posts.map { it.copy(alreadySeeCount = it.alreadySeeCount + 1) })
    }

    fun writePost(userId: UUID, channelPath: ChannelPath, content: PostContent): Post {
        val postId = PostId(userId, channelPath, UUID.randomUUID())
        val toSeeCount = calculatePostSeeCount(userId, channelPath)
        val post = Post(postId, toSeeCount, postContent = content)
        return postRepo.save(post)
    }

    private fun calculatePostSeeCount(userId: UUID, channelPath: ChannelPath): Int {
        val channel = channelRepository.findById(channelPath).orElseThrow { IllegalArgumentException() }
        return max(1, channel.peopleNumber * (1 / 10))
    }

}