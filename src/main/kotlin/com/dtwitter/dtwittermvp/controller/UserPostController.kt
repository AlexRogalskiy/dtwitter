package com.dtwitter.dtwittermvp.controller

import com.dtwitter.dtwittermvp.model.ChannelPath
import com.dtwitter.dtwittermvp.model.mongo.Channel
import com.dtwitter.dtwittermvp.model.mongo.Post
import com.dtwitter.dtwittermvp.model.mongo.User
import com.dtwitter.dtwittermvp.model.request.ChangeUserChannelsRequest
import com.dtwitter.dtwittermvp.model.request.CreateChannelRequest
import com.dtwitter.dtwittermvp.model.request.CreatePostRequest
import com.dtwitter.dtwittermvp.model.request.CreateUserRequest
import com.dtwitter.dtwittermvp.service.Service
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController("/api/v1/user")
class UserController(val service: Service) {
    @PostMapping
    fun createUser(@RequestBody createUserRequest: CreateUserRequest): ResponseEntity<User> {
        return ResponseEntity.ok(service.createUser(createUserRequest.username))
    }

    @PostMapping
    fun changeUserChannels(@RequestBody changeUserChannelsRequest: ChangeUserChannelsRequest): ResponseEntity<Unit> {
        return ResponseEntity.ok(service.changeChannels(changeUserChannelsRequest.userId, changeUserChannelsRequest.channelIds))
    }

    @GetMapping("/{userId}/channel/{channelPath}")
    fun getPosts(@PathVariable userId: UUID, @PathVariable channelPath: ChannelPath): ResponseEntity<List<Post>> {
        return ResponseEntity.ok(service.getPosts(userId, channelPath))
    }

}

@RestController("/api/v1/channel")
class ChannelController(val service: Service) {
    @PostMapping
    fun createChannel(@RequestBody createChannelRequest: CreateChannelRequest): ResponseEntity<Channel> {
        return ResponseEntity.ok(service.createChannel(createChannelRequest.userId, createChannelRequest.parentChannelPath, createChannelRequest.parentChannelPath))
    }

    @DeleteMapping
    fun deleteChannels() = ResponseEntity.ok(service.deleteChannels())

    @GetMapping
    fun getAllChannels() = ResponseEntity.ok(service.getChannels())
}

@RestController("/api/v1/post")
class PostController(val service: Service) {
    @PostMapping
    fun createPost(createPostRequest: CreatePostRequest): ResponseEntity<Post> {
        return ResponseEntity.ok(service.writePost(createPostRequest.userId, createPostRequest.channelPath, createPostRequest.content))
    }
}