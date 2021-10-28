package com.example.demo.backend.entities

data class User(
    val id: Int?,
    val username: String?,
    val password: String?,
    val token: String?,
    val full_name: String?,
    val dayOff: Float?,
    val manager_email: String?,
    val manager_name: String?,
    val is_user: Boolean?,
    val is_manager: Boolean?,
    val is_admin: Boolean?,
    val is_active: Boolean?,
    val email: String?,
    val token_device: String?
){
    constructor(username: String, password: String): this(
        null, username, password, null, null, null, null, null, null, null, null, null, null, null
    )

    constructor(): this(
        null, null, null, null, null, null, null, null, null, null, null, null, null, null
    )

    constructor(full_name: String?, email: String , token_device: String?): this(
        null, null, null, null, full_name, null, null, null, null, null, null, null, email, token_device
    )
}
