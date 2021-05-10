package com.example.gmshmsdemo.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.gmshmsdemo.BuildConfig
import com.example.gmshmsdemo.model.maps.RouteBody
import com.example.gmshmsdemo.model.maps.RouteResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class Network {

    private var logger: HttpLoggingInterceptor = HttpLoggingInterceptor()
    private var client: OkHttpClient
    private var moshi: Moshi
    private var retrofit: Retrofit

    enum class NetworkState {
        SUCCESS,
        LOADING,
        ERROR
    }

    init {
        logger.level = HttpLoggingInterceptor.Level.BODY
        client = OkHttpClient.Builder().addInterceptor(logger).build()
        moshi = Moshi.Builder().add(KotlinJsonAdapterFactory())
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl("https://mapapi.cloud.huawei.com/mapApi/v1/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    private val _mapService = retrofit.create(MapsService::class.java)


    suspend fun getRoute(
        route: RouteBody,
        type:String,
        onSuccess: (RouteResponse) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        _networkCurrentState.postValue(NetworkState.LOADING)
        var routeResponse: RouteResponse?
        try {
            routeResponse = _mapService.getRoute(type, BuildConfig.API_KEY,route)
            onSuccess(routeResponse)
            _networkCurrentState.postValue(NetworkState.SUCCESS)

        } catch (e: Throwable) {
            onError(e.message.toString())
            _networkCurrentState.postValue(NetworkState.ERROR)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: Network? = null
        private lateinit var _networkCurrentState: MutableLiveData<NetworkState>
        val networkCurrentState: LiveData<NetworkState> get() = _networkCurrentState

        fun getNetworkProvider(): Network {

            return INSTANCE ?: synchronized(this) {
                _networkCurrentState = MutableLiveData<NetworkState>()
                Network()
            }
        }
    }

}