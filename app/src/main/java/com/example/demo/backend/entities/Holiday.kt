package com.example.demo.backend.entities

import java.util.*

data class Holiday (
    val id: Int?,
    val startDate: Date?,
    val endDate: Date?,
    val title: String?,
    val numberDay: Int?
){
    constructor(): this(
        null, null, null, null, null
    )
}