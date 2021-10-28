package com.example.demo.backend

import com.example.demo.Constant.Companion.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient {
    companion object{
        private var retrofit: Retrofit? = null

        fun getClient(): Retrofit{
            if(retrofit == null){
                retrofit = Retrofit.Builder().baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()).build()
            }

            return retrofit!!
        }

        fun<T> buildService(service: Class<T>): T{
            return retrofit!!.create(service)
        }
    }
}