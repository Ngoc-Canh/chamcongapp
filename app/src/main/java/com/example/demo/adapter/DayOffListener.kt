package com.example.demo.adapter

import com.example.demo.backend.entities.DayOffEntities

interface DayOffListener {
    fun onDayOffChange(list :ArrayList<DayOffEntities>)

    fun onDayCheckAll()
}