package com.dtwitter.dtwittermvp.repository

import com.dtwitter.dtwittermvp.model.ChannelPath
import com.dtwitter.dtwittermvp.model.mongo.*
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository: MongoRepository<User, UUID>

@Repository
interface UserReadPostRepository: MongoRepository<UserReadPost, PostId>

/////////////////////////////////////////////////////////////////////////////////////////////

@Repository
interface ChannelRepository: MongoRepository<Channel, ChannelPath>

/////////////////////////////////////////////////////////////////////////////////////////////

@Repository
interface PostRepository: MongoRepository<Post, PostId>