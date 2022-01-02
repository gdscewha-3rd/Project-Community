package com.example.leafy2.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.leafy2.MainActivity
import com.example.leafy2.MyApplication
import com.example.leafy2.R
import com.example.leafy2.databinding.ActivityAuthBinding
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class AuthActivity : AppCompatActivity() {
    lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MyApplication.auth = FirebaseAuth.getInstance()

        binding.apply {
            logoutBtn.setOnClickListener {
                MyApplication.auth.signOut()
                MyApplication.email = null

            }

            loginBtn.setOnClickListener {
                login()
            }

            signupBtn.setOnClickListener {
                signBtn.visibility = View.VISIBLE
                usernameTextfield.visibility = View.VISIBLE
                signupBtn.visibility = View.GONE
                loginBtn.visibility = View.GONE
                googleLoginBtn.visibility = View.GONE
                logoutBtn.visibility = View.GONE
            }

            signBtn.setOnClickListener {
                signup()
            }
        }

    }
    private fun login(){
        val email: String = binding.emailEt.text.toString()
        val password: String = binding.passwordEt.text.toString()
        MyApplication.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                binding.emailEt.text?.clear()
                binding.passwordEt.text?.clear()
                if(task.isSuccessful){
                    MyApplication.email = email
                    Toast.makeText(baseContext, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }else{
                    Toast.makeText(baseContext, getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun signup(){
        val email: String = binding.emailEt.text.toString()
        val password: String = binding.passwordEt.text.toString()
        val username: String = binding.usernameEt.text.toString()
        MyApplication.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                binding.emailEt.text?.clear()
                binding.passwordEt.text?.clear()
                binding.usernameEt.text?.clear()
                if(task.isSuccessful){
                    MyApplication.email = email
                    MyApplication.username = username
                    Toast.makeText(baseContext, getString(R.string.signup_success), Toast.LENGTH_SHORT).show()

                }else{
                    Toast.makeText(baseContext, getString(R.string.failed_to_signup), Toast.LENGTH_SHORT).show()
                }
            }

    }
}