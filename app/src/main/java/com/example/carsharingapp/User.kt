package com.example.carsharingapp

data class User(
    var email:String,
    var first_name:String,
    var last_name:String,
    var phone:String,
    var token:String,
    var active:Int
){
    fun getFullName():String{
        return "$first_name $last_name"
    }
}
