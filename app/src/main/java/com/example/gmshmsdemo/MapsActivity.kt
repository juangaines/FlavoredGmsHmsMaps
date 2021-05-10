package com.example.gmshmsdemo

import LocationHelper
import MapHelper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.gmshmsdemo.fragments.AddDialogPosition
import com.example.gmshmsdemo.model.maps.Coordinates
import com.example.gmshmsdemo.model.maps.RouteBody
import com.example.gmshmsdemo.network.Result
import com.example.gmshmsdemo.utils.UtilsAndroid
import com.example.gmshmsdemo.utils.UtilsAndroid.Companion.REQUEST_TURN_DEVICE_LOCATION_ON
import com.huawei.hms.site.api.SearchResultListener
import com.huawei.hms.site.api.SearchService
import com.huawei.hms.site.api.SearchServiceFactory
import com.huawei.hms.site.api.model.DetailSearchRequest
import com.huawei.hms.site.api.model.DetailSearchResponse
import com.huawei.hms.site.api.model.SearchStatus
import com.huawei.hms.site.api.model.Site
import com.huawei.hms.site.widget.SearchIntent
import kotlinx.android.synthetic.huawei.activity_maps.*
import java.net.URLEncoder


class MapsActivity : AppCompatActivity() {

    private lateinit var mMap: MapHelper
    private lateinit var mLocation: LocationHelper
    private lateinit var viewModel: MapViewModel
    private var color:Int?=null
    private lateinit var searchIntent: SearchIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        viewModel = ViewModelProvider(this).get(MapViewModel::class.java)
        mMap = MapHelper(this) { location->
            viewModel.addPosition(location)
        }
        mLocation = LocationHelper(this,mMap)
        checkPermissionsLocation()
        initObservables()
        initViews()
    }

    private fun initViews() {
        searchSite.setOnClickListener {
            searchIntent = SearchIntent()
            val apiKeyEncoded=URLEncoder.encode(BuildConfig.API_KEY, "UTF-8")
            searchIntent.setApiKey(apiKeyEncoded)
            val intent = searchIntent.getIntent(this)
            startActivityForResult(intent, SearchIntent.SEARCH_REQUEST_CODE)
        }
    }

    private fun initObservables() {
        viewModel.responseRoute.observe(this, Observer {result->

            when(result){
                is Result.Success->{
                    val route=result.data!!.routes[0].paths[0].steps.forEach {step->
                        mMap.addPolyline(step.polyline,color?:Color.BLUE)
                    }
                }
                is Result.Error->{
                    Toast.makeText(this, result.exception, Toast.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.listOfMarkers.observe(this, Observer { coordinates->

            if(coordinates.size>=2){
                mMap.moveCameraWithBounds(coordinates)
                val dialog=AddDialogPosition.newInstance(coordinates[0],coordinates[1]) {type->
                    viewModel.getRoute(
                        RouteBody(
                            Coordinates(
                                coordinates[0].latitude!!,
                                coordinates[0].longitude
                            ),
                            Coordinates(
                                coordinates[1].latitude!!,
                                coordinates[1].longitude
                            )
                        ),type)
                    viewModel.restart()
                }
                dialog.show(supportFragmentManager,"dialog_map")
            }
        })

        viewModel.drawingColor.observe(this, Observer {
            color=it
        })
    }

    private fun checkPermissionsLocation() {
        if (UtilsAndroid.foregroundAndBackgroundLocationPermissionApproved(this)) {
            mLocation.checkLocationSolver(true,this,{
                mMap.enableLocation(true)
            },{
                Toast.makeText(this,"Location must be enabled to continue", Toast.LENGTH_SHORT).show()
            })

        } else {
            UtilsAndroid.requestForegroundAndBackgroundLocationPermissions(this)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_map, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem)=mMap.menuSelectionForMap(item,this)


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        Log.d(TAG, "onRequestPermissionResult")

        if (
            grantResults.isEmpty() ||
            grantResults[UtilsAndroid.LOCATION_PERMISSION_INDEX] == PackageManager.PERMISSION_DENIED ||
            (requestCode == UtilsAndroid.REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE &&
                    grantResults[UtilsAndroid.BACKGROUND_LOCATION_PERMISSION_INDEX] ==
                    PackageManager.PERMISSION_DENIED)
        ) {
            // Permission denied.

            Log.d(TAG, "Permission denied")
            startActivity(Intent().apply {
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
            Toast.makeText(this,"Permission must be granted to continue", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            this.recreate()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TURN_DEVICE_LOCATION_ON) {
            this.recreate()
        }

        if (SearchIntent.SEARCH_REQUEST_CODE == requestCode) {
            if (SearchIntent.isSuccess(resultCode)) {
                val site: Site = searchIntent.getSiteFromIntent(data)
                val apiKeyEncoded=URLEncoder.encode(BuildConfig.API_KEY, "UTF-8")
                val searchService: SearchService = SearchServiceFactory.create(this, apiKeyEncoded)
                val request = DetailSearchRequest()
                request.siteId = site.siteId
                request.language = "es"
                val resultListener: SearchResultListener<DetailSearchResponse> = object : SearchResultListener<DetailSearchResponse> {
                    // Return search results upon a successful search.
                    override fun onSearchResult(result: DetailSearchResponse?) {
                        var siteWithInfo: Site? = null
                        if (result == null || result.getSite().also { siteWithInfo = it } == null) {
                            return
                        }
                        mMap.addMarker(siteWithInfo?.location?.lat!!, siteWithInfo?.location?.lng!!, site.name,site.formatAddress)
                        Log.i("TAG", "siteId: ${siteWithInfo?.getSiteId()}, name: ${siteWithInfo?.getName()}")
                    }

                    override fun onSearchError(status: SearchStatus) {
                        Log.i("TAG", "Error : ${status.getErrorCode()}  ${status.getErrorMessage()}")
                    }
                }
                searchService.detailSearch(request, resultListener)

            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        mLocation.removeCallback()
    }

    companion object{
        val TAG = MapsActivity::class.java.simpleName
    }

}