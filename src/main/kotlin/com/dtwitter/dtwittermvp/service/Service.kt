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
import java.sql.Timestamp
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit
import java.util.*
import javax.annotation.PostConstruct

@Service
class Service(val postRepo: PostRepository,
              val userRepository: UserRepository,
              val channelRepository: ChannelRepository,
              val userReadPostRepository: UserReadPostRepository,
              val postRateProducer: PostRateProducer) {

//    @PostConstruct
    fun init() {
        val postContent = PostContent("test text")
        val postId = { PostId(UUID.randomUUID(), "/test", UUID.randomUUID()) }
        val post = { shift: Int ->
            Post(
                id = postId(),
                timestamp = Timestamp.from(Instant.now().minus(shift.toLong(), ChronoUnit.MINUTES)),
                toSeeCount = 20,
                postContent = postContent,
                thumbUp = shift
            )
        }

        postRepo.saveAll((1..10).map { post(it) })
    }

    fun createUser(username: String) {
        val user = User(username = username)
        userRepository.save(user)
    }

    fun createChannel(userOwnerId: UUID, channelName: String, parent: ChannelPath = "") {
        val channel = Channel("$parent/$channelName")
        channelRepository.save(channel)
        val user = userRepository.findById(userOwnerId).orElseThrow { IllegalArgumentException() }
        userRepository.save(
            user.copy(
                channelRates = user.channelRates + (channel.id to 0.0),
                readingChannels = user.readingChannels + channel.id)
        )
    }

    fun changeChannels(userId: UUID, channelIds: Set<ChannelPath>) {
        val user = userRepository.findById(userId).orElseThrow { IllegalArgumentException() }
        val existingChannelRates = user.channelRates.filter { it.key in channelIds }
        val newChannelRates = channelIds.filter { existingChannelRates[it] == null }.associateWith { 0.0 }
        userRepository.save(user.copy(readingChannels = channelIds, channelRates = existingChannelRates + newChannelRates))
    }

    fun ratePost(userId: UUID, postId: PostId, postRate: POST_RATE) {
        postRateProducer.sendPost(PostRate(userId, postId, postRate))
    }

    fun getPosts(userId: UUID, channelPath: ChannelPath): List<Post> {
        val readPosts = userReadPostRepository.findByIdUserIdAndIdChannelId(userId, channelPath)
        val posts = postRepo.findPostToRead(channelPath, readPosts.map { it.id.postId }, PageRequest.of(0, 5))
        return postRepo.saveAll(posts.map { it.copy(alreadySeeCount = it.alreadySeeCount + 1) })
    }

    fun writePost(userId: UUID, channelPath: ChannelPath, content: PostContent) {
        val postId = PostId(userId, channelPath, UUID.randomUUID())
        val toSeeCount = calculatePostSeeCount(userId, channelPath)
        val post = Post(postId, toSeeCount, postContent = content)
        postRepo.save(post)
    }

    fun calculatePostSeeCount(userId: UUID, channelPath: ChannelPath): Int {
        return 10 // TODO
    }

}