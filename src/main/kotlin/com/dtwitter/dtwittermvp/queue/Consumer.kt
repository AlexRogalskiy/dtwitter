package com.dtwitter.dtwittermvp.queue

import com.dtwitter.dtwittermvp.config.POST_RATE_TOPIC_NAME
import com.dtwitter.dtwittermvp.model.kafka.PostRate
import mu.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component


@Component
class Consumer {
    private val log = KotlinLogging.logger {}

    @KafkaListener(id = "postRateGroup", topics = [POST_RATE_TOPIC_NAME])
    fun listen(post: PostRate) {
        log.info("Received: $post")
    }
}