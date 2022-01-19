package com.example.leafy2.diagnosis

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PorterDuffXfermode
import android.media.Image
import android.os.Build
import android.os.Bundle
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
import com.example.leafy2.R
import com.example.leafy2.databinding.FragmentDiagnosisBinding
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.TensorOperator
import org.tensorflow.lite.support.common.TensorProcessor
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp
import org.tensorflow.lite.support.label.TensorLabel
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File
import java.io.FileInputStream
import java.lang.Exception
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.*
import kotlin.math.min

class DiagnosisFragment : Fragment() {

    val CAMERA = arrayOf(android.Manifest.permission.CAMERA)
    val CAMERA_REQUEST = 98

    private lateinit var binding: FragmentDiagnosisBinding

    private lateinit var resultTv: TextView
    private lateinit var image: ImageView

    protected lateinit var tflite: Interpreter
    private lateinit var inputImageBuffer: TensorImage
    private lateinit var probabilityProcessor: TensorProcessor
    private lateinit var outputProbabilityBuffer: TensorBuffer

    private var imageSizeX: Int = 0
    private var imageSizeY: Int = 0
    private lateinit var bitmap: Bitmap
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

        try{
            tflite = loadModel(this)?.let { Interpreter(it) }!!
        }catch (e: Exception){
            e.printStackTrace()
        }

    }

    fun loadModel(frag: DiagnosisFragment): MappedByteBuffer?{
        val fileDescriptor: AssetFileDescriptor? = frag.activity?.assets?.openFd("model.tflite")
        val inputStream: FileInputStream = FileInputStream(fileDescriptor?.fileDescriptor)
        val fileChannel: FileChannel = inputStream.channel
        val startOffset: Long? = fileDescriptor?.startOffset
        val declaredLength: Long? = fileDescriptor?.declaredLength

        if(startOffset!=null&&declaredLength!=null)
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)

        return null
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
                    bitmap = data?.extras?.get("data") as Bitmap
                    image?.setImageBitmap(bitmap)
                    classifyImage()
                }
            }
        }
    }

    fun classifyImage(){
        var imageTensorIndex = 0
        var imageShape = tflite.getInputTensor(imageTensorIndex).shape()
        imageSizeX = imageShape[2]
        imageSizeY = imageShape[1]
        val imageDataType = tflite.getInputTensor(imageTensorIndex).dataType()

        val probabilityTensorIndex = 0
        val probabilityShape = tflite.getOutputTensor(probabilityTensorIndex).shape()
        val probabilityDataType = tflite.getOutputTensor(probabilityTensorIndex).dataType()

        inputImageBuffer = TensorImage(imageDataType)
        outputProbabilityBuffer = TensorBuffer.createFixedSize(probabilityShape, probabilityDataType)
        probabilityProcessor = TensorProcessor.Builder().add(getPostProcessNormalizeOp()).build()

        inputImageBuffer = loadImage(bitmap)

        tflite.run(inputImageBuffer.buffer, outputProbabilityBuffer.buffer.rewind())
        showResult()
    }

    private fun loadImage(bitmap: Bitmap): TensorImage {
        inputImageBuffer.load(bitmap)

        val cropSize = min(bitmap.width, bitmap.height)

        val imageProcessor: ImageProcessor =
            ImageProcessor.Builder()
                .add(ResizeWithCropOrPadOp(cropSize, cropSize))
                .add(ResizeOp(imageSizeX, imageSizeY, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
                .add(getPreProcessNormalizeOp())
                .build()

        return imageProcessor.process(inputImageBuffer)
    }

    private fun getPostProcessNormalizeOp(): TensorOperator{
        return NormalizeOp(0.0f, 255.0f)
    }
    private fun getPreProcessNormalizeOp(): TensorOperator{
        return NormalizeOp(0.0f, 1.0f)
    }

    fun showResult(){
        //labels = context?.let { FileUtil.loadLabels(it, "label.txt") } as List<String>
        try {
            labels = activity?.let { FileUtil.loadLabels(it, "label.txt") } as List<String>
        }catch (e: Exception){
            e.printStackTrace()
        }

        val labeledProbability: Map<String, Float> = TensorLabel(labels, probabilityProcessor.process(outputProbabilityBuffer)).mapWithFloatValue


        val maxValueInMap = (Collections.max(labeledProbability.values))
        var resultLabelTxt: String?=null

        for(entry in labeledProbability.entries){
            if(entry.value==maxValueInMap){
                resultLabelTxt = entry.key
                setResultTv(resultLabelTxt)
            }
        }
    }

    fun setResultTv(label: String){
        when(label){
            "건강" -> {
                resultTv.setText(getString(R.string.result_healthy))
            }
            "화상" -> {
                resultTv.setText(getString(R.string.result_sunburn))
            }
            "과습" -> {
                resultTv.setText(getString(R.string.result_overwatered))
            }
            "수분부족" ->{
                resultTv.setText(getString(R.string.result_dry))
            }
        }
        binding.btnContainer.visibility = View.VISIBLE
    }
}