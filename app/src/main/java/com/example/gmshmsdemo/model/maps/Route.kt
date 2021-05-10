package com.example.gmshmsdemo.model.maps

import com.squareup.moshi.Json

data class Coordinates(
    @Json(name = "lat") val lat: Double,
    @Json(name = "lng") val lng: Double
)


data class RouteBody(
    @Json(name = "origin") val origin: Coordinates,
    @Json(name = "destination") val destination: Coordinates
)