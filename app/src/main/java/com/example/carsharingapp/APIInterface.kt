package com.example.carsharingapp

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface APIInterface {


    @Headers("Content-Type: application/json")
    @POST("api/register")
    fun createUser(@Body user:RegistrationUser):Call<RegistrationResponseUser>
}