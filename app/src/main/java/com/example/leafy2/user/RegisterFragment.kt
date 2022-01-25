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
import com.example.leafy2.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class RegisterFragment : Fragment() {

    lateinit var binding: FragmentRegisterBinding

    private lateinit var mFirebaseAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase
    private lateinit var mDatabaseReference: DatabaseReference

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var username: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mFirebaseAuth = Firebase.auth
        mDatabase = Firebase.database
        mDatabaseReference = mDatabase.getReference("users")

        binding.apply {
            email = emailEt
            password = passwordEt
            username = usernameEt

            signBtn.setOnClickListener { register() }
            cancelBtn.setOnClickListener { cancel() }

        }
    }

    private fun register(){
        val e = email.text.toString()
        val pw = password.text.toString()
        val un = username.text.toString()
        if(checkIfValid(e, pw)){
            if(un.equals(null)||un==""){
                Toast.makeText(requireContext(), "Username을 입력하세요.", Toast.LENGTH_SHORT).show()
            }else{
                mFirebaseAuth.createUserWithEmailAndPassword(e, pw)
                    .addOnCompleteListener(requireActivity()){ task ->
                        if(task.isSuccessful){
                            val firebaseUser = mFirebaseAuth.currentUser!!
                            val account = UserData(firebaseUser.uid, e, pw, un)

                            mDatabaseReference.child(firebaseUser.uid).setValue(account)

                            Toast.makeText(requireContext(), getString(R.string.signup_success), Toast.LENGTH_LONG).show()
                            goToLogin()
                        }else{
                            Toast.makeText(requireContext(), getString(R.string.failed_to_signup), Toast.LENGTH_SHORT).show()
                        }
                    }
                email.text.clear()
                password.text.clear()
                username.text.clear()
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

    private fun goToLogin(){
        findNavController().navigate(R.id.action_registerFragment_to_authFragment)
    }

    private fun cancel(){
        findNavController().navigate(R.id.action_registerFragment_to_startFragment)
    }

}