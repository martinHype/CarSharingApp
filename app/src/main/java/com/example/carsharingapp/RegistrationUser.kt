package com.example.carsharingapp

data class RegistrationUser(
    var email:String,
    var username:String,
    var first_name:String,
    var last_name:String,
    var mobile_phone:String,
    var password:String,
    var password2:String
)

data class RegistrationResponseUser(
    var respone:String,
    var token:String
)
