package com.example.gmshmsdemo.model.maps

import com.example.gmshmsdemo.model.maps.EndLocationX
import com.example.gmshmsdemo.model.maps.Polyline
import com.example.gmshmsdemo.model.maps.StartLocationX

data class Step(
    val action: String,
    val distance: Double,
    val distanceText: String,
    val duration: Double,
    val durationText: String,
    val endLocation: EndLocationX,
    val instruction: String,
    val orientation: Int,
    val polyline: List<Polyline>,
    val roadName: String,
    val startLocation: StartLocationX
)