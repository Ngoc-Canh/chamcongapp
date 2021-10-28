package com.example.demo.backend

import com.example.demo.backend.entities.*
import retrofit2.Call
import retrofit2.http.*

interface RestAPI {
    @POST("auth/login")
    fun login(@Body user: User) : Call<User>

    @GET("auth/info")
    fun info(@Header("Authorization") token: String): Call<User>

    @GET("core/event")
    fun getEvent(@Header("Authorization") token: String) : Call<Event>

    @POST("core/event/")
    fun postEvent(@Header("Authorization") token: String, @Body user: User) : Call<Event>

    @POST("core/checkin/")
    fun checkIn(@Header("Authorization") token: String): Call<String>

    @POST("core/checkout/")
    fun checkOut(@Header("Authorization") token: String): Call<String>

    @GET("core/validate")
    fun validateNetWork(@Header("Authorization") token: String): Call<Event>

    @GET("core/history")
    fun history(@Header("Authorization") token: String,
                @Query("month") month: String,
                @Query("year") year: String): Call<Event>

    @GET("core/submission")
    fun listSubmission(@Header("Authorization") token: String): Call<Event>

    @POST("core/submission")
    fun createSubmission(@Header("Authorization") token: String, @Body event: EventEntities): Call<EventEntities>

    @DELETE("core/submission/{id}")
    fun deleteSubmission(@Header("Authorization") token: String, @Path("id") id: Int): Call<EventEntities>

    @GET("core/dayOff")
    fun listDayOff(@Header("Authorization") token: String): Call<ListDayOffEntities>

    @POST("core/dayOff")
    fun createDayOff(@Header("Authorization") token: String, @Body data: DayOffEntities): Call<DayOffEntities>

    @DELETE("core/dayOff/{id}")
    fun deleteDayOff(@Header("Authorization") token: String, @Path("id") id: Int): Call<DayOffEntities>

    @GET("core/approveDayOff")
    fun getApproveDayOff(@Header("Authorization") token: String): Call<ListDayOffEntities>

    @POST("core/approveDayOff")
    fun postApproveDayOff(@Header("Authorization") token: String,
                          @Body list: ListDayOffEntities,
                          @Query("accept") accept: Boolean): Call<ListDayOffEntities>

    @GET("core/approveSubmission")
    fun getApproveSubmission(@Header("Authorization") token: String): Call<ListSubmission>

    @GET("auth/list_user")
    fun getListUser(@Header("Authorization") token: String): Call<ListUser>

    @POST("auth/create_user")
    fun createUser(@Header("Authorization") token: String, @Body user: User): Call<User>
}