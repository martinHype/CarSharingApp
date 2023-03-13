package com.example.carsharingapp

import java.time.LocalDate

data class RegistrationUser(
    var first_name:String,
    var last_name:String,
    var email:String,
    var phone_number:String,
    var password:String
)

data class Dates(
    var value:Int,
    var date: LocalDate,
    var disabled:Boolean = false
)

data class rangeDatesResponse(
    var start_date: String,
    var end_date: String
)

data class rangeDates(
    var start_date: LocalDate,
    var end_date: LocalDate
)

data class Response(
    var response:String
)


data class RegistrationResponseUser(
    var response:String,
    var token:String
)

data class LoginInUser(
    var email:String,
    var password:String
)

data class Image(
    var name:String,
    var value:String
)


data class CarRegister(
    var brand:String,
    var model:String,
    var color:String,
    var year:String,
    var owner_email:String,
    var registration_plate:String,
    var price:Int,
    var latitude:Double,
    var longitude:Double
)

data class LoginInResponseUser(
    var response:Boolean,
    var email:String,
    var first_name:String,
    var last_name:String,
    var phone_number: String,
    var token:String
)

data class UploadImage(
    var car_id:Int,
    var list:List<Image>
)

data class MapboxResponse(
    var type:String,
    var query: List<Double>,
    var features: List<Features>,
    var attribution:String
)

data class Features(
    var id:String,
    var type: String,
    var place_name:String
)

data class Brands(
    var brands:Array<String>
)

data class Brand(
    var brand:String
)

data class Models(
    var models:Array<String>
)


data class CarSpecs(
    var id:Int,
    var label:String,
    var value:String,
    var unit:String
)

data class CarId(
    var car_id: Int
)
data class CarImages(
    var images:Array<String>
)

data class CarTerms(
    var label:String,
    var value:Int
)

data class postSpecs(
    var car_id: Int,
    var list: ArrayList<Specs>
)

data class postTerms(
    var car_id:Int,
    var list:ArrayList<Terms>
)

data class ResponseSpecs(
    var specs: List<CarSpecs>,
    var terms: List<CarTerms>
)
data class Specs(
    var spec_id:Int,
    var value: String
)

data class Terms(
    var term_id:Int,
    var value: Int
)

data class RentCar(
    var car_id:Int,
    var email:String,
    var start_date:String,
    var end_date: String
)



