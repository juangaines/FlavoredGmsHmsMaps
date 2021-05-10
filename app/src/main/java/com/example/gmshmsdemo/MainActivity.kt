package com.example.gmshmsdemo

//import com.google.android.gms.common.GoogleApiAvailability
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.gmshmsdemo.databinding.ActivityMainBinding
import com.huawei.hms.api.ConnectionResult
import com.huawei.hms.api.HuaweiApiAvailability
import com.huawei.hms.jos.AppUpdateClient


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var client: AppUpdateClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.goToMap.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
    }

    fun isHmsAvailable(context: Context?): Boolean {
        var isAvailable = false
        if (null != context) {
            val result =
                HuaweiApiAvailability.getInstance().isHuaweiMobileServicesAvailable(context)
            isAvailable = ConnectionResult.SUCCESS == result
        }
        Log.i("MainActivity", "isHmsAvailable: $isAvailable")
        return isAvailable
    }

}
