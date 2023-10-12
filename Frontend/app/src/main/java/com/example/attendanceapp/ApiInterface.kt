package com.example.attendanceapp

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiInterface {
    @POST("login")
    fun userLogin(@Body data:LoginDetails):Call<ResponseModel>

    @POST("register")
    fun userRegister(@Body data:LoginDetails):Call<ResponseModel>

    @POST("candidate/add")
    fun addCandidate(@Body data:Candidate,@Header("token") token:String):Call<MyData>

    @GET("candidate/find")
    fun getCandidate(@Header("token") token:String):Call<MyData>
}