package com.dtwitter.dtwittermvp.queue

import com.dtwitter.dtwittermvp.config.POST_RATE_TOPIC_NAME
import com.dtwitter.dtwittermvp.config.POST_TO_SEE_UPDATE_TOPIC_NAME
import com.dtwitter.dtwittermvp.model.POST_RATE
import com.dtwitter.dtwittermvp.model.kafka.PostRate
import com.dtwitter.dtwittermvp.model.mongo.PostId
import com.dtwitter.dtwittermvp.repository.PostRepository
import mu.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component


@Component
class PostRateConsumer(val postRepository: PostRepository, val postIdProducer: PostIdProducer) {
    private val log = KotlinLogging.logger {}

    @KafkaListener(id = "postRateGroup", topics = [POST_RATE_TOPIC_NAME])
    fun listenPostRate(postRate: PostRate) {
        log.info("Received: $postRate")
        val post = postRepository.findById(postRate.postId).orElseThrow { IllegalArgumentException() }
        if (postRate.postRate == POST_RATE.UP) { // TODO
            postRepository.save(post.copy(thumbUp = post.thumbUp + 1))
        } else {
            postRepository.save(post.copy(thumbDown = post.thumbDown + 1))
        }
        postIdProducer.sendPostToSeeUpdate(postRate.postId)
    }

    @KafkaListener(id = "postUpdateToSeeGroup", topics = [POST_TO_SEE_UPDATE_TOPIC_NAME])
    fun listenPostToSeeUpdate(postId: PostId) {
        log.info("Received: $postId")
    }
}