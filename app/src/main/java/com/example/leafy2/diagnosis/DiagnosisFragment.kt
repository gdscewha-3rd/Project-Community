package com.example.leafy2.diagnosis

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.tool.util.FileUtil
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.FileUtils
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.tool.util.FileUtil
import com.example.leafy2.R
import com.example.leafy2.databinding.FragmentDiagnosisBinding


class DiagnosisFragment : Fragment() {

    val CAMERA = arrayOf(android.Manifest.permission.CAMERA)
    val CAMERA_REQUEST = 98

    private lateinit var binding: FragmentDiagnosisBinding

    private lateinit var resultTv: TextView
    private lateinit var image: ImageView
    private lateinit var labels: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDiagnosisBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        resultTv = binding.resultTv
        binding.captureIv.setOnClickListener { takePhoto() }
        image = binding.captureIv
    }

    private fun takePhoto(){
        if(checkPermission(CAMERA)){
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, CAMERA_REQUEST)
        }
    }

    private fun checkPermission(permissions: Array<out String>): Boolean{
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            for( permission in permissions){
                if(context?.let { ContextCompat.checkSelfPermission(it, permission) } != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(context as Activity, permissions, CAMERA_REQUEST)
                    return false
                }
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            CAMERA_REQUEST -> {
                for(grant in grantResults){
                    if(grant != PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(context, "카메라 권한을 허용해주세요", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==RESULT_OK){
            when (requestCode){
                CAMERA_REQUEST -> {
                    val bitmap = data?.extras?.get("data") as Bitmap
                    image?.setImageBitmap(bitmap)
                }
            }
        }
    }

    fun classifyImage(){
        labels = FileUtil.load
    }
}