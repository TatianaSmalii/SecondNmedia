package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.User
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.UnknownError
import ru.netology.nmedia.model.AuthModel
import ru.netology.nmedia.util.SingleLiveEvent

class SignInViewModel(application: Application) : AndroidViewModel(application) {
    private var user = User(0L, "")

    private val _signInErrorState = MutableLiveData(AuthModel())
    val signInErrorState: LiveData<AuthModel>
        get() = _signInErrorState

    private val _signedIn = SingleLiveEvent<Unit>()
    val signedIn: LiveData<Unit>
        get() = _signedIn

    private var code: Int = 0

    fun clearErrorText() {
        _signInErrorState.value = AuthModel()
    }

    private suspend fun checkUser(login: String, password: String) {
        try {
            code = 0
            val response = PostsApi.retrofitService.auth(login, password)
            if (!response.isSuccessful) {
                if (response.code() == 404) {
                    code = response.code()
                }
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())

            user = user.copy(id = body.id, token = body.token)
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    fun signIn(login: String, password: String) {
        viewModelScope.launch {
            try {
                checkUser(login, password)
                AppAuth.getInstance().setAuth(user.id, user.token)
                _signInErrorState.value = AuthModel()
                _signedIn.value = Unit
            } catch (e: Exception) {
                if (code == 404) {
                    _signInErrorState.value = AuthModel(wrongData = true)
                } else {
                    _signInErrorState.value = AuthModel(unableSingIn = true)
                }
            }
        }
    }
}


