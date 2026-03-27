package com.photogram.core.model

data class StoryReply(
    val id: String,
    val storyId: String,
    val authorId: String,
    val content: String,
    val createdAt: Long,
)
