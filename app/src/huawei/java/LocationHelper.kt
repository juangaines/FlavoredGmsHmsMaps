import android.content.IntentSender
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.gmshmsdemo.LocationOperations
import com.example.gmshmsdemo.MapOperations
import com.example.gmshmsdemo.utils.UtilsAndroid
import com.huawei.hmf.tasks.OnFailureListener
import com.huawei.hmf.tasks.OnSuccessListener
import com.huawei.hms.common.ResolvableApiException
import com.huawei.hms.location.*


class LocationHelper:LocationOperations {

    private var settingsClient: SettingsClient?
    private var fusedLocationClient: FusedLocationProviderClient?
    private val mLocationCallback:LocationCallback

    private val locationRequest = LocationRequest.create().apply {
        priority = LocationRequest.PRIORITY_HD_ACCURACY
        interval = 10000
    }

    constructor(activity: AppCompatActivity,mapHelper: MapOperations) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        settingsClient = LocationServices.getSettingsClient(activity)

        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                mapHelper.animateCamera(locationResult.lastLocation)
            }
        }

        fusedLocationClient!!
            .requestLocationUpdates(locationRequest, mLocationCallback, Looper.getMainLooper())
            .addOnSuccessListener {
                //Processing when the API call is successful.
            }
            .addOnFailureListener {
                //Processing when the API call fails.
            }
    }

    override fun checkLocationSolver(
        resolve: Boolean,
        activity: AppCompatActivity,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(activity)
        val locationSettingsResponseTask =
            settingsClient.checkLocationSettings(builder.build())

        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    exception.startResolutionForResult(
                        activity,
                        UtilsAndroid.REQUEST_TURN_DEVICE_LOCATION_ON
                    )
                    onError()
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.d(TAG, "Error getting location settings resolution: " + sendEx.message)
                }
            } else {
                Log.d(TAG, "Fail location resolution")
            }
        }.addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess()
            }
        }
    }

    override fun removeCallback(){
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