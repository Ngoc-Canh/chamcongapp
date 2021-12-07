package com.example.demo.backend.entities

data class EventEntities(
    val created_at: Long?,
    val event_type: String?,
    val created_date: CustomDate,
    val reason: String?,
    val status: String?
)
