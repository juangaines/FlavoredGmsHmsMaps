package com.example.gmshmsdemo.model.maps

data class RouteResponse(
    val returnCode: String,
    val returnDesc: String,
    val routes: List<RouteX>
)