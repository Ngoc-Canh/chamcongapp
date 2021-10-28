package com.example.demo.backend.entities

import java.util.*

data class DayOffEntities(
    val id: Int?,
    var ts_start_for_start_date: Long?,
    var ts_end_for_start_date: Long?,
    var ts_start_for_end_date: Long?,
    var ts_end_for_end_date: Long?,
    var start_date: Date?,
    var end_date: Date?,
    var type: String?,
    var type_code: String?,
    var created_by: String?,
    var created_date: Date?,
    var total_dayOff: Float?,
    var status: Boolean?,
    var manager_confirm_code: Boolean?,
    var manager_confirm: String?
){
    constructor(): this(
        null,null, null,null, null,
        null, null, null,null ,null ,
        null ,null, null, null, null
    )
}
