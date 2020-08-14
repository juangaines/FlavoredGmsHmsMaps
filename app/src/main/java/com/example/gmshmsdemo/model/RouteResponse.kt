package com.example.gmshmsdemo.model

data class RouteResponse(
    val returnCode: String,
    val returnDesc: String,
    val routes: List<RouteX>
)