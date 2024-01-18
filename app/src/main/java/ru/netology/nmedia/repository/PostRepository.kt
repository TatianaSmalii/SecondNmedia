package ru.netology.nmedia.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.PhotoModel

interface PostRepository {
    val data: Flow<List<Post>>
    suspend fun getAll()
    fun getNewerCount(): Flow<Int>
    suspend fun updateNewPosts()
    suspend fun save(post: Post)
    suspend fun saveWithAttachment(post: Post, photo: PhotoModel?)
    suspend fun removeById(id: Long)
    suspend fun likeById(post: Post)

    suspend fun saveEditedPost(post: Post)
    suspend fun getUnsavedPosts(): List<Post>
    suspend fun deleteUnsavedPosts()
}