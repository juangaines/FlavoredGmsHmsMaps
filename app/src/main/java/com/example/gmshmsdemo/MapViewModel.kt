package com.example.gmshmsdemo

import android.app.Application
import android.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.gmshmsdemo.model.maps.LandMarkObject
import com.example.gmshmsdemo.model.maps.RouteBody
import com.example.gmshmsdemo.model.maps.RouteResponse
import com.example.gmshmsdemo.network.Network
import com.example.gmshmsdemo.network.Result
import com.example.gmshmsdemo.utils.UtilsAndroid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MapViewModel(application: Application) : AndroidViewModel(application) {

    private var viewModelJob: Job = Job()
    private var couroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)
    private val _networkService = Network.getNetworkProvider()
    val networkState: LiveData<Network.NetworkState> get() = Network.networkCurrentState
    private val _responseRoute =
        MutableLiveData<Result<RouteResponse>>()
    private val _listOfMarkers = MutableLiveData<ArrayList<LandMarkObject>>()

    private val _drawingColor= MutableLiveData<Int>()
    val listOfMarkers:LiveData<ArrayList<LandMarkObject>> get() = _listOfMarkers
    val responseRoute:LiveData<Result<RouteResponse>> get() = _responseRoute
    val drawingColor:LiveData<Int> get() = _drawingColor


    init {
        _listOfMarkers.value= ArrayList()
        _drawingColor.value= Color.BLUE
    }

    fun addPosition(location: LandMarkObject){
        _listOfMarkers.value?.add(location)
        _listOfMarkers.value=_listOfMarkers.value
    }

    fun getRoute(
        route: RouteBody,
        type:String
    ) {

        _drawingColor.value=UtilsAndroid.decideColor(type)

        couroutineScope.launch {
            _networkService.getRoute(route,type,onSuccess={
                _responseRoute.postValue(Result.Success(it))
            },
            onError = {
                _responseRoute.postValue(Result.Error(it))
            })
        }
    }

    fun restart(){
        _listOfMarkers.value?.clear()
        _listOfMarkers.value=_listOfMarkers.value
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}