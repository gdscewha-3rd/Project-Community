package com.example.leafy2.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.leafy2.R
import com.example.leafy2.databinding.FragmentStartBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import org.json.JSONObject


class StartFragment : Fragment() {

    companion object {
        const val API_KEY: String = "ac5471e3caa6df5bb40fbe111f57c735"
        const val WEATHER_URL: String = "https://api.openweathermap.org/data/2.5/weather"
        const val MIN_TIME: Long = 5000
        const val MIN_DISTANCE: Float = 1000F
        const val WEATHER_REQUEST: Int = 102
    }


    private lateinit var binding: FragmentStartBinding

    private lateinit var weatherTip: TextView
    private lateinit var weatherIcon: ImageView

    private lateinit var mLocationManager: LocationManager
    private lateinit var mLocationListener: LocationListener

    private lateinit var mDatabaseRef: DatabaseReference

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentStartBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.startFragment = this


        viewModel.temperature.observe(
            viewLifecycleOwner, { temperature -> binding.temperatureTv.text = temperature + " ℃" }
        )
        viewModel.weatherState.observe(
            viewLifecycleOwner, { weatherState -> binding.weatherTv.text = weatherState }
        )

        weatherTip = binding.weatherTipTv
        weatherIcon = binding.weatherIc

        binding.toCalendar.setOnClickListener { goToCalendarFragment() }

        binding.toChat.setOnClickListener { goToChatbotFragment() }

        binding.toDiagnose.setOnClickListener { goToDiagnosisFragment() }

        binding.toNews.setOnClickListener { goToNewsFragment() }

        mDatabaseRef = FirebaseDatabase.getInstance().reference


    }

    override fun onStart() {
        super.onStart()

        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {

            mDatabaseRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val username =
                        (snapshot.child("users").child(user.uid).child("userName").getValue())
                    setGreetingText(username as String)

                    viewModel.setUsername(username)

                    val email = (snapshot.child("users").child(user.uid).child("email").getValue())
                    viewModel.setEmail(email as String)


                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.layout_menu, menu)

        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
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

    private fun getWeatherInCurrentLocation() {
        mLocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

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
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                WEATHER_REQUEST
            )
            return
        }
        mLocationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            MIN_TIME,
            MIN_DISTANCE,
            mLocationListener
        )
        mLocationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            MIN_TIME,
            MIN_DISTANCE,
            mLocationListener
        )
    }


    private fun doNetworking(params: RequestParams) {
        val client = AsyncHttpClient()

        client.get(WEATHER_URL, params, object : JsonHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                response: JSONObject?
            ) {
                viewModel.fromJson(response)
                updateWeather()
            }
        })
    }

    private fun updateWeather() {
        val resourceID =
            resources.getIdentifier(viewModel.weatherIcon.value, "drawable", activity?.packageName)
        weatherIcon.setImageResource(resourceID)
        weatherTip.text = getNewTip()
    }

    private fun getNewTip(): String {
        return when (viewModel.weatherTip.value) {
            0 -> getString(R.string.weather_hot)
            1 -> getString(R.string.weather_good)
            2 -> getString(R.string.weather_humid)
            3 -> getString(R.string.weather_snow)
            4 -> getString(R.string.weather_gray)
            5 -> getString(R.string.weather_error)
            else -> getString(R.string.weather_cold)
        }
    }

    override fun onPause() {
        super.onPause()
        mLocationManager.removeUpdates(mLocationListener)
    }

    private fun goToChatbotFragment() {
        findNavController().navigate(R.id.action_startFragment_to_chatbotFragment)
    }

    private fun goToDiagnosisFragment() {
        findNavController().navigate(R.id.action_startFragment_to_diagnosisFragment)
    }

    private fun goToCalendarFragment() {
        findNavController().navigate(R.id.action_startFragment_to_calendarFragment)
    }

    private fun goToLoginActivity() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            // 로그인 상태, 유저 정보 페이지로 이동
            findNavController().navigate(R.id.action_startFragment_to_userInfoFragment)
            // Toast.makeText(requireContext(), "you are already logged in", Toast.LENGTH_SHORT).show()
            // 임시 로그아웃, 유저 정보 페이지에서 로그아웃 하도록 수정
            // Firebase.auth.signOut()
        } else {
            // 로그인 페이지로 이동
            findNavController().navigate(R.id.action_startFragment_to_authFragment)
        }

    }

    private fun goToNewsFragment() {
        findNavController().navigate(R.id.action_startFragment_to_newsFragment)
    }


    fun setGreetingText(username: String) {
        binding.greetingTv.text = username + "님 안녕하세요 :)"
    }
}