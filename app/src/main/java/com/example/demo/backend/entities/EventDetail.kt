package com.example.demo.backend.entities

import java.util.*

data class EventDetail(
    val id: Int?,
    val created_at: Long?,
    val event_type: String?,
    val valid: String?,
    val reason: String?,
    val created_date: Date?,
    val status: String?,
    val manager_confirm: String?,
    val haveSubmission: Boolean?
){
    constructor(): this(
        null, null, null, null, null, null,null,null, null
    )
}
