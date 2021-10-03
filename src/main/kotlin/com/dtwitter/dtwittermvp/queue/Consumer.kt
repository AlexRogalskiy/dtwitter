package com.dtwitter.dtwittermvp.queue

import com.dtwitter.dtwittermvp.model.mongo.Post
import mu.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component


@Component
class Consumer {
    private val log = KotlinLogging.logger {}

    @KafkaListener(id = "postRateGroup", topics = ["post_rate_topic"])
    fun listen(post: Post) {
        log.info("Received: $post")
    }
}