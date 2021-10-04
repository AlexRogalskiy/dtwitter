package com.dtwitter.dtwittermvp.repository

import com.dtwitter.dtwittermvp.model.ChannelPath
import com.dtwitter.dtwittermvp.model.mongo.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository: MongoRepository<User, UUID>

@Repository
interface UserReadPostRepository: MongoRepository<UserReadPost, PostId> {
    fun findByIdUserIdAndIdChannelId(userId: UUID, channelPath: ChannelPath): List<UserReadPost>
}

/////////////////////////////////////////////////////////////////////////////////////////////

@Repository
interface ChannelRepository: MongoRepository<Channel, ChannelPath>

/////////////////////////////////////////////////////////////////////////////////////////////

@Repository
interface PostRepository: MongoRepository<Post, PostId> {
    @Query(value = "{\"_id.channelId\": ?0, \$expr:{\$gt:[\"\$toSeeCount\", \"\$alreadySeeCount\"]}, \"_id.postId\": {\$nin: [ ?1 ]}}", sort = "{ timestamp: -1 }")
    fun findPostToRead(channelPath: ChannelPath, excludedPostIds: List<UUID>, page: Pageable): Page<Post>
}