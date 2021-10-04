package com.dtwitter.dtwittermvp.model.kafka

import com.dtwitter.dtwittermvp.model.POST_RATE
import com.dtwitter.dtwittermvp.model.mongo.PostId
import java.util.*


data class PostRate(
    val ratedUserId: UUID,
    val postId: PostId,
    val postRate: POST_RATE
)