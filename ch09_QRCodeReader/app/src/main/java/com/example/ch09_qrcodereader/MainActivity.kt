package com.example.ch09_qrcodereader

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.ch09_qrcodereader.databinding.ActivityMainBinding
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private var isDetected = false

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    private val PERMISSION_REQUEST_CODE = 1
    private val PERMISSION_REQUIRED = arrayOf(Manifest.permission.CAMERA)

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!hasPermissions(this)) {
            requestPermissions(PERMISSION_REQUIRED, PERMISSION_REQUEST_CODE)
        } else {
            startCamera()
        }
    }

    private fun hasPermissions(context: Context) = PERMISSION_REQUIRED.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (PackageManager.PERMISSION_GRANTED == grantResults.firstOrNull()) {
                startCamera()
            } else {
                Toast.makeText(this@MainActivity, "PERMISSION_DENIED", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        isDetected = false
    }

    private fun startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()

            val preview = getPreview()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            val imageAnalysis = getImageAnalysis()

            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
        }, ContextCompat.getMainExecutor(this))
    }

    private fun getPreview(): Preview {
        val preview = Preview.Builder().build()
        preview.setSurfaceProvider(binding.barcodePreview.surfaceProvider)
        return preview
    }

    private fun getImageAnalysis(): ImageAnalysis {
        val imageAnalysis = ImageAnalysis.Builder().build()
        imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(),
            QRCodeAnalyzer(object: OnDetectListener {
                override fun onDetect(msg: String) {
                    if (!isDetected) {
                        isDetected = true
                        val intent = Intent(this@MainActivity, ResultActivity::class.java)
                        intent.putExtra("msg", msg)
                        startActivity(intent)
                    }
                }
        }))
        return imageAnalysis
    }
}