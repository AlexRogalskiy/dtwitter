package com.dtwitter.dtwittermvp.service

import com.dtwitter.dtwittermvp.model.mongo.Content
import com.dtwitter.dtwittermvp.model.mongo.Post
import com.dtwitter.dtwittermvp.model.mongo.PostId
import com.dtwitter.dtwittermvp.queue.Producer
import com.dtwitter.dtwittermvp.repository.PostRepository
import org.springframework.stereotype.Service
import java.util.*
import javax.annotation.PostConstruct

@Service
class Service(val postRepo: PostRepository,
              val producer: Producer) {

    @PostConstruct
    fun test() {
        val post = Post(
            PostId(UUID.randomUUID(), "channel.sport.chelsea", UUID.randomUUID()),
            20,
            content = Content("test text")
        )

//        postRepo.save(post)
//        producer.sendPost(post)

    }
}