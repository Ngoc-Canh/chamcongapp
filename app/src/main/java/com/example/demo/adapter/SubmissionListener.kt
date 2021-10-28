package com.example.demo.adapter

import com.example.demo.backend.entities.Submission

interface SubmissionListener {
    fun onSubmissionChange(list :ArrayList<Submission>)

    fun onSubmissionCheckAll()
}