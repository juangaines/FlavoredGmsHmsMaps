package com.example.gmshmsdemo.model.maps

import com.example.gmshmsdemo.model.maps.Bounds
import com.example.gmshmsdemo.model.maps.Path

data class RouteX(
    val bounds: Bounds,
    val paths: List<Path>
)