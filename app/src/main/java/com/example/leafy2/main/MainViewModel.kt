package com.example.leafy2.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.json.JSONException
import org.json.JSONObject

class MainViewModel: ViewModel() {
    private val _weatherState = MutableLiveData("")
    val weatherState: LiveData<String> get() = _weatherState
    private val _weatherId = MutableLiveData(0)
    val weatherId: LiveData<Int> get() = _weatherId
    private val _temperature = MutableLiveData("")
    val temperature: LiveData<String> get() = _temperature
    private val _tempInt = MutableLiveData(0)
    val tmpInt: LiveData<Int> get() = _tempInt
    private val _weatherTip = MutableLiveData(0)
    val weatherTip: LiveData<Int> get() = _weatherTip
    private val _weatherIcon = MutableLiveData("")
    val weatherIcon: LiveData<String> get() = _weatherIcon

    init {
        _weatherTip.value = getNewTipID()
    }


    fun fromJson(jsonObject: JSONObject?) {
        try{
            _weatherId.value = jsonObject?.getJSONArray("weather")?.getJSONObject(0)?.getInt("id")!!
            _weatherState.value = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main")
            _weatherIcon.value = _weatherId.value?.let { updateWeatherIcon(it) }

            val roundedTemp: Int = (jsonObject.getJSONObject("main").getDouble("temp")-273.15).toInt()
            _temperature.value = roundedTemp.toString()

        }catch (e: JSONException){
            e.printStackTrace()

        }
    }

    private fun updateWeatherIcon(condition: Int): String {
        if (condition in 200..299) {
            return "thunderstorm"
        } else if (condition in 300..499) {
            return "lightrain"
        } else if (condition in 500..599) {
            return "rain"
        } else if (condition in 600..700) {
            return "snow"
        } else if (condition in 701..771) {
            return "fog"
        } else if (condition in 772..799) {
            return "overcast"
        } else if (condition == 800) {
            return "clear"
        } else if (condition in 801..804) {
            return "cloudy"
        } else if (condition in 900..902) {
            return "thunderstorm"
        }
        if (condition == 903) {
            return "snow"
        }
        if (condition == 904) {
            return "clear"
        }
        return if (condition in 905..1000) {
            "thunderstorm"
        } else "dunno"

    }

    private fun getNewTipID(): Int{
        var newTip: Int = 0

        if(_tempInt.value!! > 35){
            newTip = 0
        }else if (_tempInt.value!! in 10..35){
            if(_weatherIcon.value=="clear"){
                newTip = 1
            }else if(_weatherIcon.value=="thunderstorm"||_weatherIcon.value=="lightrain"||_weatherIcon.value=="rain"){
                newTip = 2
            }else if(_weatherIcon.value=="snow"){
                newTip = 3
            }else if(_weatherIcon.value=="cloudy"||_weatherIcon.value=="fog"||_weatherIcon.value=="overcast"){
                newTip = 4
            }else{
                newTip = 5
            }
        }else{
            newTip = 6
        }
        return newTip
    }
}