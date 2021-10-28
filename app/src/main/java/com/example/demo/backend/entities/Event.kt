package com.example.demo.backend.entities

data class Event(
    val event: ArrayList<EventDetail>?,
    val holiday: ArrayList<HolidayDetail>?,
    val day_off: ArrayList<DayOffDetail>?,
    val user: User?,
    val valid_ip: Boolean?,
    val is_holiday: Boolean?,
    val is_day_off: Boolean?
){
    constructor(): this(
        null, null, null,null, false, false, false
    )
}