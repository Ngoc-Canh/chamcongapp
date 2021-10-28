package com.example.demo.backend.entities

data class Submission(
    val id: Int?,
    val created_by: String?,
    val created_at: String?,
    val created_date: String?,
    val type: String?,
    val reason: String?,
    val status: Boolean?,
    val manager_confirm: String?
)
