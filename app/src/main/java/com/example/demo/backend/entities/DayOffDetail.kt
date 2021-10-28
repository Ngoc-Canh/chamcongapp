package com.example.demo.backend.entities

import java.util.*

data class DayOffDetail(
    val date: Date?,
    val all_day: Boolean?,
    val morning: Boolean?,
    val afternoon: Boolean?,
    val type: String?,
    val status: String?
){
    constructor(): this(
        null, null, null, null, null, null
    )
}
