import android.annotation.SuppressLint
import android.content.IntentSender
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.gmshmsdemo.MapOperations
import com.example.gmshmsdemo.model.LandMarkObject
import com.example.gmshmsdemo.utils.UtilsAndroid
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener

class LocationHelper{

    private var landMarkObject: LandMarkObject?=null
    private  var settingsClient: SettingsClient?
    private  var fusedLocationClient: FusedLocationProviderClient
    private val mLocationCallback:LocationCallback

    private val locationRequest = LocationRequest.create().apply {
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        interval=10000
    }

    @SuppressLint("MissingPermission")
    constructor(activity: AppCompatActivity,mapHelper: MapOperations){
        fusedLocationClient= LocationServices.getFusedLocationProviderClient(activity)
        settingsClient = LocationServices.getSettingsClient(activity)

        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult != null) {
                    mapHelper.animateCamera(locationResult.lastLocation)
                }
            }
        }
        fusedLocationClient!!
            .requestLocationUpdates(locationRequest, mLocationCallback, Looper.getMainLooper())
            .addOnSuccessListener {
                //Processing when the API call is successful.
            }
            .addOnFailureListener{
                //Processing when the API call fails.
            }

    }

    fun checkLocationSolver(resolve:Boolean,
                            activity: AppCompatActivity,
                            onSuccess: () -> Unit,
                            onError: () -> Unit){

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(activity)
        val locationSettingsResponseTask =
            settingsClient.checkLocationSettings(builder.build())

        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    exception.startResolutionForResult(activity,
                        UtilsAndroid.REQUEST_TURN_DEVICE_LOCATION_ON)
                    onError()
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.d(TAG, "Error getting location settings resolution: " + sendEx.message)
                }
            } else {
                Log.d(TAG, "Fallamos a lo mal papu")
            }
        }.addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess()
            }
        }
    }

    fun removeCallback(){
        fusedLocationClient!!.removeLocationUpdates(mLocationCallback)
            .addOnSuccessListener {

            }
            .addOnFailureListener {

            }
    }



    companion object {
        val TAG = LocationHelper::class.simpleName
    }
}