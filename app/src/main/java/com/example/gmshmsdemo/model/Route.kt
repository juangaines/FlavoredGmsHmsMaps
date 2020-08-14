package com.example.gmshmsdemo.model

import com.squareup.moshi.Json

data class Origin(
    @Json(name = "lat") val lat: Double,
    @Json(name = "lng") val lng: Double
)

data class Destination(
    @Json(name = "lat") val lat: Double,
    @Json(name = "lng") val lng: Double
)

data class RouteQuery(
    @Json(name = "origin") val origin: Origin,
    @Json(name = "destination") val destination: Destination
)