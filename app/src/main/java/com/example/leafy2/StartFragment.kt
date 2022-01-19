package com.example.leafy2

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.leafy2.databinding.FragmentStartBinding
import com.example.leafy2.login.AuthActivity
import com.google.gson.Gson
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import org.json.JSONObject


class StartFragment : Fragment() {

    val API_KEY: String = "ac5471e3caa6df5bb40fbe111f57c735"
    val WEATHER_URL: String = "https://api.openweathermap.org/data/2.5/weather"
    val MIN_TIME: Long = 5000
    val MIN_DISTANCE: Float = 1000F
    val WEATHER_REQUEST: Int = 102

    private var binding: FragmentStartBinding?= null
    private lateinit var weatherState: TextView
    private lateinit var temperature: TextView
    private lateinit var weatherTip: TextView
    private lateinit var weatherIcon: ImageView

    private lateinit var mLocationManager: LocationManager
    private lateinit var mLocationListener: LocationListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentStartBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.startFragment = this
        binding?.apply {
            temperature = temperatureTv
            weatherState = weatherTv
            weatherTip = weatherTipTv
            weatherIcon = weatherIc
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.layout_menu, menu)

        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.user_setting -> {
                goToLoginActivity()
                return true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getWeatherInCurrentLocation()
    }

    private fun getWeatherInCurrentLocation(){
        mLocationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        mLocationListener = LocationListener { p0 ->
            val params: RequestParams = RequestParams()
            params.put("lat", p0.latitude)
            params.put("lon", p0.longitude)
            params.put("appid", API_KEY)
            doNetworking(params)
        }

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION), WEATHER_REQUEST)
            return
        }
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener)
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener)
    }



    private fun doNetworking(params: RequestParams) {
        var client = AsyncHttpClient()

        client.get(WEATHER_URL, params, object: JsonHttpResponseHandler(){
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                response: JSONObject?
            ) {
                val weatherData = WeatherData().fromJson(response)
                if (weatherData != null) {
                    updateWeather(weatherData)
                }
            }
        })
    }

    private fun updateWeather(weather: WeatherData) {
        temperature.setText(weather.tempString+" ℃")
        weatherState.setText(weather.weatherType)
        val resourceID = resources.getIdentifier(weather.icon, "drawable", activity?.packageName)
        weatherIcon.setImageResource(resourceID)
        weatherTip.setText(getNewTip(weather.tempInt, weather.icon))
    }

    private fun getNewTip(temperature: Int, weather: String): String{
        var newTip: String = ""
        if(temperature>35){
            newTip = getString(R.string.weather_hot)
        }else if (temperature in 10..35){
            if(weather=="clear"){
                newTip = getString(R.string.weather_hot)
            }else if(weather=="thunderstorm"||weather=="lightrain"||weather=="rain"){
                newTip = getString(R.string.weather_humid)
            }else if(weather=="snow"){
                newTip = getString(R.string.weather_snow)
            }else if(weather=="cloudy"||weather=="fog"||weather=="overcast"){
                newTip = getString(R.string.weather_gray)
            }else{
                newTip = weather+getString(R.string.weather_error)
            }
        }else{
            newTip = getString(R.string.weather_cold)
        }
        return newTip
    }

    override fun onPause() {
        super.onPause()
        if(mLocationManager!=null){
            mLocationManager.removeUpdates(mLocationListener)
        }
    }

    fun goToChatbotFragment(){
        findNavController().navigate(R.id.action_startFragment_to_chatbotFragment)
    }

    fun goToDiagnosisFragment(){
        findNavController().navigate(R.id.action_startFragment_to_diagnosisFragment)
    }

    fun setGreetingText(){
        binding?.greetingTv?.setText(MyApplication.username+"님 안녕하세요 : )")
    }

    fun goToLoginActivity(){
        activity?.let{
            val intent = Intent(context, AuthActivity::class.java)
            startActivity(intent)
        }
    }
}