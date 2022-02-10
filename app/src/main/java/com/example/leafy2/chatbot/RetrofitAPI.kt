package com.example.leafy2.chatbot

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface RetrofitAPI {
    @GET()
    fun getMessage(@Url url: String): Call<MessageModel>
}