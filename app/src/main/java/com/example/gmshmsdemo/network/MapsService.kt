package com.example.gmshmsdemo.network

import com.example.gmshmsdemo.model.RouteQuery
import com.example.gmshmsdemo.model.RouteResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MapsService {

    @POST("routeService/{type}")
    suspend fun getRoute(
        @Path("type") path:String,
        @Query ("key")key:String,
        @Body route: RouteQuery
    ): RouteResponse

}