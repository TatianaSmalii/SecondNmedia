package ru.netology.nmedia.repository


import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.dto.Post
import java.util.concurrent.TimeUnit

class PostRepositoryImpl : PostRepository {
    // Сетевой вызов с тайм-аутом 30 секунд для обработки
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()
    }

    override fun getAll(): List<Post> {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        val call = client.newCall(request)
        val response = call.execute()
        val body = requireNotNull(response.body)
        val responseText = body.string()
        return gson.fromJson(responseText, typeToken)
    }

    override fun save(post: Post): Post {
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/slow/posts")
            .build()

        val call = client.newCall(request)
        val response = call.execute()
        val body = requireNotNull(response.body)
        val responseText = body.string()
        return gson.fromJson(responseText, Post::class.java)
    }

    override fun likeById(id: Long): Post {
        val post = getAll().find { it.id == id }
        val request: Request =
            if (post?.likedByMe == false) {
                Request.Builder()
                    .post(gson.toJson(post).toRequestBody(jsonType))
                    .url("${BASE_URL}/api/posts/$id/likes")
                    .build()
            } else {
                Request.Builder()
                    .delete()
                    .url("${BASE_URL}/api/posts/$id/likes")
                    .build()
            }

        val call = client.newCall(request)
        val response = call.execute()
        val body = requireNotNull(response.body)
        val responseText = body.string()
        return gson.fromJson(responseText, Post::class.java)
    }

    override fun share(id: Long) {
        // TODO: do this in homework
    }

    override fun removeById(id: Long) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }
}