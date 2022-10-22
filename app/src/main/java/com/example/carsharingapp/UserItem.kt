package com.example.carsharingapp

data class UserItem(
    val email: String,
    val first_name: String,
    val last_name: String,
    val password: String,
    val username: String,
){
    fun getString():String{
        return this.username + "\n" + this.email + "\n" + this.first_name + "\n" + this.last_name + "\n" + this.password+"\n"
    }
}