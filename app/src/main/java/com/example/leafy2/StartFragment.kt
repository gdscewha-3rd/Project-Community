package com.example.leafy2

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.leafy2.databinding.FragmentStartBinding
import com.example.leafy2.login.AuthActivity


class StartFragment : Fragment() {

    private var binding: FragmentStartBinding?= null

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

    fun goToChatbotFragment(){
        findNavController().navigate(R.id.action_startFragment_to_chatbotFragment)
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