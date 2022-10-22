package com.example.carsharingapp

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface APIInterface {

    @GET("get_users?format=json")
    fun getData(): Call<List<UserItem>>

    @Headers("Content-Type: application/json")
    @POST("register")
    fun createUser(@Body user:User):Call<User>
}