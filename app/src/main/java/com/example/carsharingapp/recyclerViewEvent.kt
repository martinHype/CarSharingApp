package com.example.carsharingapp

import java.time.LocalDate

interface recyclerViewEvent {
    fun onItemClick(position: Int)
    fun onImageClick(position: Int)
}

interface recyclerViewDateEvent{
    fun onItemClick(date: LocalDate, position:Int)
}