package com.example.demo.backend.entities

import java.time.LocalDate

data class EventEntities(
    val created_at: Long?,
    val event_type: String?,
    val created_date: LocalDate,
    val reason: String?,
    val status: String?
)
