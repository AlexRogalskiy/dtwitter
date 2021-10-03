package com.dtwitter.dtwittermvp.model

typealias ChannelPath = String

enum class ChannelStatus {
    PENDING,
    CREATED,
    DELETED
}

enum class UserMessageStatus {
    READ,
    NOT_READ
}

enum class RATE {
    UP,
    DOWN
}