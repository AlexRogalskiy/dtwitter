package com.dtwitter.dtwittermvp.queue

import com.dtwitter.dtwittermvp.config.POST_RATE_TOPIC_NAME
import com.dtwitter.dtwittermvp.model.mongo.Post
import mu.KotlinLogging
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component


@Component
class Producer(private val kafkaTemplate: KafkaTemplate<String, Post>) {
    private val log = KotlinLogging.logger {}

    fun sendPost(post: Post) {
        log.info(String.format("#### -> Producing message -> %s", post))
        kafkaTemplate.send(POST_RATE_TOPIC_NAME, post)
    }
}