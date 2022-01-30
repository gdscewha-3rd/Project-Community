package com.example.leafy2.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.leafy2.R
import com.example.leafy2.databinding.FragmentUserInfoBinding
import com.example.leafy2.main.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class UserInfoFragment : Fragment() {

    private lateinit var binding : FragmentUserInfoBinding
    private lateinit var mFirebaseAuth: FirebaseAuth

    private val viewModel: MainViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mFirebaseAuth = Firebase.auth

        binding.apply {
            usernameTv.text = viewModel.username.value
            emailTv.text = viewModel.email.value

            logoutBtn.setOnClickListener { logout() }
        }
    }

    private fun logout(){
        // mFirebaseAuth.signOut()
        findNavController().navigate(R.id.action_userInfoFragment_to_startFragment)
    }
}