package com.dtwitter.dtwittermvp.queue

import com.dtwitter.dtwittermvp.config.POST_RATE_TOPIC_NAME
import com.dtwitter.dtwittermvp.model.kafka.PostRate
import mu.KotlinLogging
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component


@Component
class PostRateProducer(private val kafkaTemplate: KafkaTemplate<String, PostRate>) {
    private val log = KotlinLogging.logger {}

    fun sendPost(post: PostRate) {
        log.info("#### -> Producing message -> $post")
        kafkaTemplate.send(POST_RATE_TOPIC_NAME, post)
    }
}