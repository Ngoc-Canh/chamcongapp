package com.example.demo.backend.entities

import java.util.*

data class HolidayDetail (
    val holiday: Date?,
    val description: String?
){
    constructor(): this(
        null, null
    )
}