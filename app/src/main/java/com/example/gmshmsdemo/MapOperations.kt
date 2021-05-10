package com.example.gmshmsdemo

import android.location.Location
import com.example.gmshmsdemo.model.maps.LandMarkObject
import com.example.gmshmsdemo.model.maps.Polyline

interface MapOperations {

    fun initialize()
    fun addMarker(lat: Double, long: Double, title: String, snippet: String? = "")
    fun setMapType(type: Int)
    fun enableLocation(enable: Boolean)
    fun setOnMapLongClickListener(callback: (LandMarkObject) -> Unit)
    fun setOnMapClickListener(callback: (LandMarkObject) -> Unit)
    fun setPoiClick()
    fun addGroundOverlay()
    fun addPolyline(polyline: List<Polyline>, color:Int)
    fun animateCamera(location: Location)
    fun clear()
}