package com.example.leafy2.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.leafy2.MainActivity
import com.example.leafy2.MyApplication
import com.example.leafy2.R
import com.example.leafy2.UserData
import com.example.leafy2.databinding.ActivityAuthBinding
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text

class AuthActivity : AppCompatActivity() {
    lateinit var binding: ActivityAuthBinding

    private lateinit var mFirebaseAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase
    private lateinit var mDatabaseReference: DatabaseReference
    private lateinit var mEmail: EditText
    private lateinit var mPassword: EditText
    private lateinit var mUsername: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // MyApplication.auth = FirebaseAuth.getInstance()
        mFirebaseAuth = Firebase.auth
        mDatabase = Firebase.database
        mDatabaseReference = mDatabase.getReference("users")

        binding.apply {

            mEmail = emailEt
            mPassword = passwordEt
            mUsername = usernameEt

            logoutBtn.setOnClickListener {
                MyApplication.auth.signOut()
                MyApplication.email = null

            }

            loginBtn.setOnClickListener {
                login()
            }

            adminAccountBtn.setOnClickListener {
                loginAsAdmin()
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
        val email: String = mEmail.text.toString()
        val password: String = mPassword.text.toString()
        if(checkIfValid(email,password)){
            mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    mEmail.text.clear()
                    mPassword.text.clear()
                    if(task.isSuccessful){
                        Toast.makeText(baseContext, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }else{
                        Toast.makeText(baseContext, getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun loginAsAdmin(){
        mFirebaseAuth.signInWithEmailAndPassword("admin@gmail.com", "admin1234")
            .addOnCompleteListener(this) { task ->
                mEmail.text.clear()
                mPassword.text.clear()
                if(task.isSuccessful){
                    Toast.makeText(baseContext, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }else{
                    Toast.makeText(baseContext, getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun signup(){
        val email: String = mEmail.text.toString()
        val password: String = mPassword.text.toString()
        val username: String = mUsername.text.toString()
        if(checkIfValid(email, password)){
            if(username.equals(null)||username==""){
                Toast.makeText(this, "Username을 입력하세요.", Toast.LENGTH_SHORT).show()
            }else{
                mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->

                        if(task.isSuccessful){
                            Log.d("createAccount", "success")
                            val firebaseUser: FirebaseUser = mFirebaseAuth.currentUser!!
                            val account = UserData(firebaseUser.uid, mEmail.text.toString(), mPassword.text.toString(), mUsername.text.toString() )


                            mDatabaseReference.child(firebaseUser.uid).setValue(account)

                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)

                            Toast.makeText(baseContext, getString(R.string.signup_success), Toast.LENGTH_SHORT).show()

                        }else{
                            Log.d("createAccount", "fail")
                            Toast.makeText(baseContext, getString(R.string.failed_to_signup), Toast.LENGTH_SHORT).show()
                        }

                        mEmail.text.clear()
                        mPassword.text.clear()
                        mUsername.text.clear()
                    }
            }
        }
    }

    private fun checkIfValid(email: String, password: String): Boolean{
        return if(email.equals(null)|| email == "" ||!email.contains('@')){
            Toast.makeText(this, "올바른 이메일을 작성해 주세요.", Toast.LENGTH_SHORT).show()
            false
        }else if(password.length<=6){
            Toast.makeText(this, "비밀번호는 6자리 이상입니다.", Toast.LENGTH_SHORT).show()
            false
        }else{
            true
        }
    }
}