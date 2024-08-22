package com.hybrid.projectarea.view

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.location.LocationManager
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.hybrid.projectarea.R
import com.hybrid.projectarea.databinding.FragmentCameraBinding
import com.hybrid.projectarea.model.DateTimeLocationManager
import com.hybrid.projectarea.model.ImageOverlay
import com.hybrid.projectarea.model.RequestPermissions
import com.hybrid.projectarea.utils.encodeImage
import com.hybrid.projectarea.utils.rotateAndCreateBitmap
import com.hybrid.projectarea.view.preproject.PreProjectFragment
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

typealias LumaListener = (luma: Double) -> Unit

class CameraFragment : Fragment() {
    private var _binding:FragmentCameraBinding? = null
    private val binding get() = _binding!!

    private lateinit var file: File
    private var latitude: Double? = null
    private var longitude: Double? = null

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCameraBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectionCamera()

        binding.captureButton.setOnClickListener {
            takePhoto()
        }
        binding.closeCamera.setOnClickListener {
            stopCamera()
            openFragment(PreProjectFragment())
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun selectionCamera() {
        if (RequestPermissions.hasPermissions(requireContext(), request_permissions)) {
            checkGpsStatus().addOnSuccessListener {
                (activity as? AppCompatActivity)?.supportActionBar?.hide()
                startCamera()
            }
            checkGpsStatus().addOnFailureListener {
                requestGPSEnable()
            }
        } else {
            requestPermissionLauncherCameraLocation.launch(
                request_permissions
            )
        }
    }

    private fun stopCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()
        }, ContextCompat.getMainExecutor(requireContext()))

        cameraExecutor.shutdown()
    }


    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.previewView.surfaceProvider)
                }
            imageCapture = ImageCapture.Builder()
                .build()

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, LuminosityAnalyzer { luma ->
                        Log.d(TAG, "Average luminosity: $luma")
                    })
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalyzer
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        createPhotoFile()
        val imageCapture = imageCapture ?: return
        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    println("Photo capture failed: ${exc.message}")
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    image()
                }
            }
        )
    }

    private class LuminosityAnalyzer(private val listener: LumaListener) : ImageAnalysis.Analyzer {

        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()    // Rewind the buffer to zero
            val data = ByteArray(remaining())
            get(data)   // Copy the buffer into a byte array
            return data // Return the byte array
        }

        override fun analyze(image: ImageProxy) {

            val buffer = image.planes[0].buffer
            val data = buffer.toByteArray()
            val pixels = data.map { it.toInt() and 0xFF }
            val luma = pixels.average()

            listener(luma)

            image.close()
        }
    }

    private fun createPhotoFile() {
        val dir = view?.context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        file = File.createTempFile("IMG_${System.currentTimeMillis()}_", ".png", dir)
    }

    private val requestPermissionLauncherCameraLocation = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraPermissionGranted = permissions[Manifest.permission.CAMERA] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false

        if (cameraPermissionGranted && coarseLocationGranted) {
            checkGpsStatus().addOnSuccessListener {
                startCamera()
            }
            checkGpsStatus().addOnFailureListener {
                requestGPSEnable()
            }
        } else {
            if (!cameraPermissionGranted) {
                Snackbar.make(
                    binding.root,
                    getString(R.string.enable_camera_permission),
                    Snackbar.LENGTH_LONG
                ).show()
            }
            if (!coarseLocationGranted) {
                Snackbar.make(
                    binding.root,
                    getString(R.string.enable_coarse_location_permission),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun checkGpsStatus(): Task<LocationSettingsResponse> {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val settingsClient = LocationServices.getSettingsClient(requireContext())
        return settingsClient.checkLocationSettings(builder.build())
    }

    fun requestGPSEnable() {
        val locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage("El GPS está desactivado, ¿quieres activarlo?")
                .setCancelable(false)
                .setPositiveButton("Sí") { dialog, id ->
                    requireContext().startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                .setNegativeButton("No") { dialog, id ->
                    dialog.cancel()
                }
            val alert = builder.create()
            alert.show()
        }
    }

    private fun image() {
        val currentDateTime = DateTimeLocationManager.getCurrentDateTime()
        DateTimeLocationManager.getCurrentLocation(requireContext()) { location ->
            if (location != null) {
                latitude = location.latitude
                longitude = location.longitude
                val mutableBitmap = rotateAndCreateBitmap(file).copy(Bitmap.Config.ARGB_8888, true)
                val logoBitmap =
                    ContextCompat.getDrawable(requireContext(), R.drawable.logo_ccip_white)
                        ?.toBitmap()
                val modifiedImage = ImageOverlay.overlayTextOnImage(
                    mutableBitmap,
                    logoBitmap!!,
                    currentDateTime,
                    latitude!!,
                    longitude!!
                )
                saveImageToMediaStore(modifiedImage, "ModifiedImage_${System.currentTimeMillis()}.jpg")
            } else {
                requestGPSEnable()
            }
        }
    }

    private fun saveImageToMediaStore(bitmap: Bitmap, fileName: String) {
        try {
            val resolver = requireContext().contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }

            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            uri?.let {
                val outputStream = resolver.openOutputStream(it)
                outputStream?.use { stream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                }
            }
            Toast.makeText(requireContext(),"Imagen guardada correctamente",Toast.LENGTH_LONG).show()
        } catch (e: IOException){
            Toast.makeText(requireContext(),"Error al guardar la imagen",Toast.LENGTH_LONG).show()
        }

    }

    private fun openFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.contenedor, fragment)
        transaction.commit()
    }

    companion object {
        private const val TAG = "CameraXApp"
        private val request_permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }
}