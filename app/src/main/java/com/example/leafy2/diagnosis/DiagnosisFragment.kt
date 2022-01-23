package com.example.leafy2.diagnosis

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap

import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import java.io.FileInputStream
import java.lang.Exception
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.*
import kotlin.math.min

import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import com.example.leafy2.UserData
import com.example.leafy2.calendar.RecordData
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat


class DiagnosisFragment : Fragment() {

    private lateinit var binding: FragmentDiagnosisBinding

    private lateinit var result: TextView
    private lateinit var image: ImageView
    private lateinit var save: Button
    private lateinit var recapture: Button
    private lateinit var click: TextView

    protected lateinit var tflite: Interpreter
    private lateinit var inputImageBuffer: TensorImage
    private lateinit var probabilityProcessor: TensorProcessor
    private lateinit var outputProbabilityBuffer: TensorBuffer

    private var imageSizeX: Int = 0
    private var imageSizeY: Int = 0
    private lateinit var bitmap: Bitmap
    private lateinit var labels: List<String>
    private lateinit var time: String

    private lateinit var mDatabaseReference: DatabaseReference
    private lateinit var mUser: FirebaseUser
    private lateinit var userId: String
    private lateinit var mStorageReference: StorageReference
    lateinit var mProfileReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDiagnosisBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            result = resultTv
            captureIv.setOnClickListener { takePhoto() }
            reCaptureBtn.setOnClickListener { takePhoto() }
            image = captureIv
            click = clickTv
            recapture = reCaptureBtn
            save = saveBtn
            saveBtn.setOnClickListener { addRecord() }
        }

        try{
            tflite = loadModel(this)?.let { Interpreter(it) }!!
        }catch (e: Exception){
            e.printStackTrace()
        }

        mDatabaseReference = Firebase.database.reference
        mUser = FirebaseAuth.getInstance().currentUser!!
        userId = if(mUser!=null) mUser.uid else ""
        mStorageReference = FirebaseStorage.getInstance().reference
        // mProfileReference = mStorageReference.child("image").child(userId).child(time)

        val animation: Animation = AnimationUtils.loadAnimation(
            requireContext(),
            com.example.leafy2.R.anim.blink
        )
        click.startAnimation(animation)

    }

    private fun addRecord(){
        Toast.makeText(requireContext(), "기록 중입니다.", Toast.LENGTH_SHORT).show()

        var baos: ByteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        mProfileReference = mStorageReference.child("image").child(userId).child(time)
        val uploadTask = mProfileReference.putBytes(data)

        uploadTask.addOnFailureListener{
            Toast.makeText(requireContext(), "업로드 실패", Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
            taskSnapshot.storage.downloadUrl.addOnCompleteListener {
                val imageUrl = it.toString()
            }
            Toast.makeText(requireContext(), "기록 완료", Toast.LENGTH_SHORT).show()
        })

    }

    private fun loadModel(frag: DiagnosisFragment): MappedByteBuffer?{
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
        if(checkPermission(Companion.CAMERA)){
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, Companion.CAMERA_REQUEST)
        }
    }

    private fun checkPermission(permissions: Array<out String>): Boolean{
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            for( permission in permissions){
                if(context?.let { ContextCompat.checkSelfPermission(it, permission) } != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(context as Activity, permissions,
                        Companion.CAMERA_REQUEST
                    )
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
            Companion.CAMERA_REQUEST -> {
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
                Companion.CAMERA_REQUEST -> {
                    bitmap = data?.extras?.get("data") as Bitmap
                    image.setImageBitmap(bitmap)
                    classifyImage()
                }
            }
        }
    }

    private fun classifyImage(){
        val imageTensorIndex = 0
        val imageShape = tflite.getInputTensor(imageTensorIndex).shape()
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

    private fun showResult(){
        try {
            labels = activity?.let { FileUtil.loadLabels(it, "label.txt") } as List<String>
        }catch (e: Exception){
            e.printStackTrace()
        }

        val labeledProbability: Map<String, Float> = TensorLabel(labels, probabilityProcessor.process(outputProbabilityBuffer)).mapWithFloatValue


        val maxValueInMap = (Collections.max(labeledProbability.values))

        for(entry in labeledProbability.entries){
            if(entry.value==maxValueInMap){
                setResultTv(entry.key)
            }
        }
    }

    fun setResultTv(label: String){
        when(label){
            "건강" -> {
                result.text = getString(com.example.leafy2.R.string.result_healthy)
            }
            "화상" -> {
                result.text = getString(com.example.leafy2.R.string.result_sunburn)
            }
            "과습" -> {
                result.text = getString(com.example.leafy2.R.string.result_overwatered)
            }
            "수분부족" ->{
                result.text = getString(com.example.leafy2.R.string.result_dry)
            }
        }
        recapture.visibility = View.VISIBLE
        save.visibility = View.VISIBLE
        click.clearAnimation()
        click.visibility = View.GONE
        val dateToday: Date = Date(System.currentTimeMillis())
        val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        time = dateFormat.format(dateToday)


    }

    companion object {
        private const val CAMERA_REQUEST = 98
        private val CAMERA = arrayOf(android.Manifest.permission.CAMERA)
    }
}