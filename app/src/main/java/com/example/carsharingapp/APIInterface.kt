package com.example.carsharingapp

import retrofit2.Call
import retrofit2.http.*

interface APIInterface {

    @Headers("Content-Type: application/json")
    @GET("api/cars/cars.php")
    fun recieveCars(@Header("Token") token:String):Call<List<Cars>>

    @Headers("Content-Type: application/json")
    @POST("api/cars/car_images.php")
    fun getImages(@Header("Token") token:String,@Body carId:CarId):Call<CarImages>

    @Headers("Content-Type: application/json")
    @POST("api/cars/create.php")
    fun createCar(@Body car:CarRegister):Call<Response>

    @Headers("Content-Type: application/json")
    @POST("api/users/create.php")
    fun createUser(@Body user:RegistrationUser):Call<RegistrationResponseUser>

    @Headers("Content-Type: application/json")
    @POST("api/users/auth.php")
    fun loginUser(@Body user:LoginInUser):Call<LoginInResponseUser>

    @Headers("Content-Type: application/json")
    @POST("api/cars/uploadimage.php")
    fun uploadImage(@Body uploadImage: UploadImage):Call<Response>

    @Headers("Content-Type: application/json")
    @GET
    fun getAdress(@Url url:String, @Query("access_token") access:String): Call<MapboxResponse>

    @Headers("Content-Type: application/json")
    @GET("api/cars/brands.php")
    fun getBrands():Call<Brands>

    @Headers("Content-Type: application/json")
    @POST("api/cars/models.php")
    fun getModels(@Body brand:Brand):Call<Models>

    @Headers("Content-Type: application/json")
    @POST("api/cars/car_specs.php")
    fun getSpecs(@Header("Token") token:String,@Body carId:CarId) :Call<ResponseSpecs>

    @Headers("Content-Type: application/json")
    @POST("api/cars/add_specs.php")
    fun addSpecs(@Header("Token") token:String,@Body postSpecs:postSpecs) :Call<Response>

    @Headers("Content-Type: application/json")
    @POST("api/cars/add_terms.php")
    fun addTerms(@Header("Token") token:String,@Body postTerms: postTerms) :Call<Response>

    @Headers("Content-Type: application/json")
    @POST("api/cars/add_rental.php")
    fun rentCar(@Header("Token") token:String,@Body rentCar: RentCar):Call<Response>

    @Headers("Content-Type: application/json")
    @POST("api/cars/rented_days.php")
    fun getRentedDays(@Header("Token") token:String,@Body carId: CarId):Call<List<rangeDatesResponse>>

}