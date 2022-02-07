package com.example.leafy2


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController

        setupActionBarWithNavController(navController)

        val auth: FirebaseAuth = Firebase.auth

        val user = FirebaseAuth.getInstance().currentUser
        if(user!=null){
            Firebase.auth.signOut()
        }

    }



    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

}