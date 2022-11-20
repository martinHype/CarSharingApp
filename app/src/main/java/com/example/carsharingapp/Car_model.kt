package com.example.carsharingapp

import com.mapbox.geojson.Point

data class Car_model(var location:Point,var car_brand:String,var car_model:String,var car_licensePlate:String,var image:Int,var markerImage:Int,var owner:String,var adress:String,var price_perDay:Int,var is_rented:Boolean)
