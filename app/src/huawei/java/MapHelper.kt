import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity

import com.example.gmshmsdemo.MapOperations
import com.example.gmshmsdemo.R
import com.example.gmshmsdemo.model.LandMarkObject
import com.example.gmshmsdemo.model.Polyline
import com.example.gmshmsdemo.utils.UtilsAndroid
import com.huawei.hms.maps.CameraUpdate
import com.huawei.hms.maps.CameraUpdateFactory

import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.HuaweiMap.MAP_TYPE_NONE
import com.huawei.hms.maps.HuaweiMap.MAP_TYPE_NORMAL
import com.huawei.hms.maps.SupportMapFragment
import com.huawei.hms.maps.model.*

class MapHelper : MapOperations {

    private lateinit var map: HuaweiMap
    private lateinit var mActivity: AppCompatActivity
    private lateinit var addLocationForMarker: (LandMarkObject) -> Unit


    constructor(
        activity: AppCompatActivity,
        addPosition: (LandMarkObject) -> Unit
    ) {
        mActivity = activity
        initialize()
        addLocationForMarker = addPosition
    }

    override fun initialize() {
        val mapFragment =
            (mActivity.supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment)
        mapFragment.getMapAsync { huaweiMap ->
            map = huaweiMap
            map.setMarkersClustering(true)
            map.setOnMapClickListener { latLong ->
                this@MapHelper.addLocationForMarker(
                    LandMarkObject(
                        latLong.latitude,
                        latLong.longitude
                    )
                )
                this.addMarker(
                    latLong.latitude,
                    latLong.longitude,
                    "Marker",
                    "lat:${latLong.latitude} lon:${latLong.longitude}"
                )
            }
            map.setMinZoomPreference(6.0f);
            map.setMaxZoomPreference(20.0f);
            this.setPoiClick()
            this.addGroundOverlay()
            map.isTrafficEnabled=true
        }

        mapFragment.retainInstance = true
    }

    override fun addMarker(lat: Double, long: Double, title: String, snippet: String?) {
        val marker = MarkerOptions().position(LatLng(lat, long)).title(title).clusterable(true)
        map.addMarker(marker).snippet = snippet
    }

    override fun setMapType(type: Int) {
        when (type) {
            MAP_TYPE_NORMAL,
            MAP_TYPE_NONE
            -> {
                map.mapType = type
            }
            else -> {
                Log.d(TAG, "No type supported for Huawei map")
            }

        }

    }

    @SuppressLint("MissingPermission")
    override fun enableLocation(enable: Boolean) {
        if (UtilsAndroid.foregroundAndBackgroundLocationPermissionApproved(mActivity)) {
            map.isMyLocationEnabled = enable
            map.uiSettings.isMyLocationButtonEnabled = enable
            map.uiSettings.isZoomControlsEnabled = enable
        } else
            UtilsAndroid.requestForegroundAndBackgroundLocationPermissions(mActivity)
    }

    override fun setOnMapLongClickListener(callback: (LandMarkObject) -> Unit) {

        map.setOnMapLongClickListener {
            callback(LandMarkObject(it.latitude, it.longitude))
        }
    }

    override fun setOnMapClickListener(callback: (LandMarkObject) -> Unit) {
        map.setOnMapClickListener {
            callback(LandMarkObject(it.latitude, it.longitude))
        }
    }

    override fun setPoiClick() {
        map.setOnPoiClickListener {
            val marker = MarkerOptions().position(it.latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .title(it.name)
            val poiMarker = map.addMarker(marker)
            poiMarker.snippet = "POI"
            poiMarker.showInfoWindow()
        }
    }

    override fun addGroundOverlay() {
        val overlaySize = 100f
        val androidOverlay = GroundOverlayOptions()
            .image(BitmapDescriptorFactory.fromResource(R.drawable.android))
            .position(LatLng(4.7103657, -74.0620457), overlaySize, overlaySize)
        map.addGroundOverlay(androidOverlay)
    }

    override fun addPolyline(polyline: List<Polyline>, color: Int) {
        val latLon = polyline.map {
            LatLng(it.lat, it.lng)
        }
        polyline.forEach {
            map.addPolyline(
                PolylineOptions()
                    .addAll(
                        latLon
                    )
                    .color(color)
                    .width(5f)
            )
        }
    }

    override fun animateCamera(location: Location) {
       if (this::map.isInitialized){
           map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude,location.longitude),map.cameraPosition.zoom ))
       }
    }

    override fun clear() {
        map.clear()
    }

    fun menuSelectionForMap(item: MenuItem, activity: AppCompatActivity): Boolean {
        return when (item.itemId) {
            R.id.normal_map -> {
                map.mapType = MAP_TYPE_NORMAL
                true
            }
            R.id.none -> {
                map.mapType = MAP_TYPE_NONE
                true
            }

            R.id.clear -> {
                clear()
                true
            }
            else -> activity.onOptionsItemSelected(item)
        }
    }

    companion object {
        val TAG = MapHelper::class.simpleName
    }


}