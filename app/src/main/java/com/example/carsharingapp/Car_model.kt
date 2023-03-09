package com.example.carsharingapp

import android.os.Parcel
import android.os.Parcelable
import com.mapbox.geojson.Point
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Car_model(
    var car_id:Int,
    var car_brand:String,
    var car_model:String,
    var car_licensePlate:String,
    var image:String,
    var owner:String,
    var price_perDay:Int,
    var is_rented:Boolean,
    var latitude:Double,
    var longitude:Double,
    var brand_url:String
    ){
    lateinit var adress:String
    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.mapbox.com/geocoding/v5/mapbox.places/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val url:String = "${longitude},${latitude}.json?"
        val response = retrofit.create(APIInterface::class.java)
        response.getAdress(url,"pk.eyJ1IjoibWFydGluYmF0IiwiYSI6ImNsMnB6a2Z1YTBidjgzZnBjeXhleGUzZDQifQ.YuflIZcligQ5Yr1or55iUg")
            .enqueue(object : Callback<MapboxResponse>{
                override fun onResponse(
                    call: Call<MapboxResponse>,
                    response: retrofit2.Response<MapboxResponse>
                ) {
                    if(response.isSuccessful){
                        adress = "${response?.body()?.features?.get(0)?.place_name}"
                    }
                }

                override fun onFailure(call: Call<MapboxResponse>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            })
    }

}
