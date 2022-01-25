package com.example.leafy2.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.leafy2.R
import com.example.leafy2.databinding.FragmentAuthBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class AuthFragment : Fragment() {

    private lateinit var binding : FragmentAuthBinding
    private lateinit var mFirebaseAuth: FirebaseAuth

    private lateinit var email: EditText
    private lateinit var password: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mFirebaseAuth = Firebase.auth

        binding.apply {
            email = emailEt
            password = passwordEt


            loginBtn.setOnClickListener {
                login()
            }

            adminAccountBtn.setOnClickListener {
                loginAsAdmin()
            }

            signupBtn.setOnClickListener {
                goToRegister()
            }

        }
    }

    private fun login(){
        val e = email.text.toString()
        val pw = password.text.toString()
        if(checkIfValid(e, pw)){
            mFirebaseAuth.signInWithEmailAndPassword(e, pw)
                .addOnCompleteListener(requireActivity()) { task ->
                    email.text.clear()
                    password.text.clear()
                    if(task.isSuccessful){
                        goToStartFragment()
                    }else{
                        Toast.makeText(requireContext(), getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
    private fun loginAsAdmin(){
        mFirebaseAuth.signInWithEmailAndPassword("admin@gmail.com", "admin1234")
            .addOnCompleteListener(requireActivity()) { task ->
                email.text.clear()
                password.text.clear()
                if(task.isSuccessful){
                    goToStartFragment()
                }else{
                    Toast.makeText(requireContext(), getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun checkIfValid(email: String, password: String): Boolean{
        return if(email.equals(null)|| email == "" ||!email.contains('@')){
            Toast.makeText(requireContext(), "올바른 이메일을 작성해 주세요.", Toast.LENGTH_SHORT).show()
            false
        }else if(password.length<6){
            Toast.makeText(requireContext(), "비밀번호는 6자리 이상입니다.", Toast.LENGTH_SHORT).show()
            false
        }else{
            true
        }
    }

    private fun goToRegister(){
        findNavController().navigate(R.id.action_authFragment_to_registerFragment)
    }
    private fun goToStartFragment(){
        findNavController().navigate(R.id.action_authFragment_to_startFragment)
    }
}