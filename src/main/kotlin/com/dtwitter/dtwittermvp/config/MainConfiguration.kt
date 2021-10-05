package com.dtwitter.dtwittermvp.config

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaOperations
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer
import org.springframework.kafka.listener.SeekToCurrentErrorHandler
import org.springframework.kafka.support.converter.RecordMessageConverter
import org.springframework.kafka.support.converter.StringJsonMessageConverter
import org.springframework.util.backoff.FixedBackOff


@Configuration
class MainConfiguration {
    @Bean
    fun errorHandler(template: KafkaOperations<Any, Any>) = SeekToCurrentErrorHandler(
        DeadLetterPublishingRecoverer(template), FixedBackOff(1000L, 2)
    )

    @Bean
    fun converter(): RecordMessageConverter = StringJsonMessageConverter()

    @Bean
    fun topic() = NewTopic(POST_RATE_TOPIC_NAME, 1, 1)

}

const val POST_RATE_TOPIC_NAME = "post_rate_topic"
const val POST_TO_SEE_UPDATE_TOPIC_NAME = "post_to_see_update_topic"