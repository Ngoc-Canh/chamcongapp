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

    @POST("core/saveTokenDevice")
    fun saveTokenDevice(@Header("Authorization") token: String, @Body user: User) : Call<Event>

    @POST("checkin/")
    fun checkIn(@Header("Authorization") token: String): Call<String>

    @POST("checkout/")
    fun checkOut(@Header("Authorization") token: String): Call<String>

    @GET("core/history")
    fun history(@Header("Authorization") token: String,
                @Query("month") month: String,
                @Query("year") year: String): Call<Event>

    @GET("submission/")
    fun listSubmission(@Header("Authorization") token: String): Call<Event>

    @POST("submission/")
    fun createSubmission(@Header("Authorization") token: String, @Body event: EventEntities): Call<EventEntities>

    @DELETE("submission/{id}")
    fun deleteSubmission(@Header("Authorization") token: String, @Path("id") id: Int): Call<EventEntities>

    @GET("dayOff/")
    fun listDayOff(@Header("Authorization") token: String): Call<ListDayOffEntities>

    @POST("dayOff/")
    fun createDayOff(@Header("Authorization") token: String, @Body data: DayOffEntities): Call<DayOffEntities>

    @DELETE("dayOff/{id}")
    fun deleteDayOff(@Header("Authorization") token: String, @Path("id") id: Int): Call<List<String>>

    @GET("approve/approveDayOff")
    fun getApproveDayOff(@Header("Authorization") token: String): Call<ListDayOffEntities>

    @POST("approve/approveDayOff")
    fun postApproveDayOff(@Header("Authorization") token: String,
                          @Body list: ListDayOffEntities,
                          @Query("accept") accept: Boolean): Call<ListDayOffEntities>

    @GET("approve/approveSubmission")
    fun getApproveSubmission(@Header("Authorization") token: String): Call<ListSubmission>

    @POST("approve/approveSubmission")
    fun postApproveSubmission(@Header("Authorization") token: String,
                              @Body list: ListSubmission,
                              @Query("accept") accept: Boolean): Call<ListSubmission>

    @GET("auth/list_user")
    fun getListUser(@Header("Authorization") token: String): Call<ListUser>

    @GET("holiday/")
    fun getListHoliday(@Header("Authorization") token: String): Call<ListHoliday>

    @POST("holiday/")
    fun createHoliday(@Header("Authorization") token: String,
                      @Body data: Holiday): Call<Holiday>

    @DELETE("holiday/{id}")
    fun deleteHoliday(@Header("Authorization") token: String, @Path("id") id: Int): Call<Holiday>

    @POST("auth/create_user")
    fun createUser(@Header("Authorization") token: String, @Body user: User): Call<User>
}