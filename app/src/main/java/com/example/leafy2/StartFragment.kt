package com.example.leafy2

import android.content.Intent
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.example.leafy2.databinding.FragmentStartBinding
import com.example.leafy2.login.AuthActivity


class StartFragment : Fragment() {

    val APP_ID: String = "ac5471e3caa6df5bb40fbe111f57c735"
    val WEATHER_URL: String = "https://api.openweathermap.org/data/2.5/weather"
    val MIN_TIME: Long = 5000
    val MIN_DISTANCE: Float = 1000F
    val WEATHER_REQUEST: Int = 101

    private var binding: FragmentStartBinding?= null
    private lateinit var weatherState: TextView
    private lateinit var temperature: TextView
    private lateinit var weatherIc: ImageView

    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



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

    }

    override fun onResume() {
        super.onResume()
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