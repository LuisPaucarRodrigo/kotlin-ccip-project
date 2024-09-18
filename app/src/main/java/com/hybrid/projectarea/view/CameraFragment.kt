package com.hybrid.projectarea.view

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.util.Size
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.OrientationEventListener
import android.view.ScaleGestureDetector
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
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
import com.hybrid.projectarea.utils.rotateAndCreateBitmap
import com.hybrid.projectarea.view.preproject.PreProjectFragment
import java.io.File
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class CameraFragment : Fragment() {
    private var _binding: FragmentCameraBinding? = null
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
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectionCamera()
        val windowManager = requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val windowMetrics = windowManager.currentWindowMetrics
        val bounds = windowMetrics.bounds
        val width = bounds.width()
        val height = bounds.height()
        println("width")
        println(width)
        println("height")
        println(height)
        println("width")
        println(width-100)
        println("height")
        println(height-100)
        binding.captureButton.setOnClickListener {
            takePhoto()
        }
        binding.closeCamera.setOnClickListener {
            stopCamera()
            openFragment(PreProjectFragment())
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    @RequiresApi(Build.VERSION_CODES.R)
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

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        val windowManager = requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val windowMetrics = windowManager.currentWindowMetrics
        val bounds = windowMetrics.bounds
        val width = bounds.width()
        val height = bounds.height()
        val screenAspectRatio = aspectRatio(width, height)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
//            val aspectRatioStrategy = AspectRatioStrategy(AspectRatio.RATIO_4_3, AspectRatio.RATIO_16_9)

//            val resolutionSelector = ResolutionSelector.Builder()
//                .setAspectRatioStrategy(aspectRatioStrategy)
//                .build()

            // Preview
            val preview = Preview.Builder()
//                .setResolutionSelector(resolutionSelector)

                .setTargetAspectRatio(screenAspectRatio)
//                .setTargetResolution(Size(width-100, height))
                .build()
                .also {
                    it.setSurfaceProvider(binding.previewView.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .setTargetAspectRatio(screenAspectRatio)
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            val orientationEventListener = object : OrientationEventListener(requireContext()) {
                override fun onOrientationChanged(orientation : Int) {
                    // Monitors orientation values to determine the target rotation value
                    val rotation : Int = when (orientation) {
                        in 45..134 -> Surface.ROTATION_270
                        in 135..224 -> Surface.ROTATION_180
                        in 225..314 -> Surface.ROTATION_90
                        else -> Surface.ROTATION_0
                    }

                    imageCapture?.targetRotation = rotation
                }
            }
            orientationEventListener.enable()

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()

            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()
            try {
                cameraProvider.unbindAll()
                val camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalyzer
                )
                val cameraControl = camera.cameraControl
                val cameraInfo = camera.cameraInfo

                // Configurar el gesto de zoom con los dedos (pinch-to-zoom)
                val scaleGestureDetector = ScaleGestureDetector(requireContext(), object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    override fun onScale(detector: ScaleGestureDetector): Boolean {
                        val currentZoomRatio = cameraInfo.zoomState.value?.zoomRatio ?: 1f
                        val delta = detector.scaleFactor
                        cameraControl.setZoomRatio(currentZoomRatio * delta)
                        return true
                    }
                })

                binding.previewView.setOnTouchListener { _, event ->
                    scaleGestureDetector.onTouchEvent(event)
                    return@setOnTouchListener true
                }

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

    private fun createPhotoFile() {
        val dir = view?.context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        file = File.createTempFile("IMG_${System.currentTimeMillis()}_", ".png", dir)
    }

    @RequiresApi(Build.VERSION_CODES.R)
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
                val bitmap = rotateAndCreateBitmap(file)
                if (bitmap != null) {
                    val mutableBitmap =
                        bitmap.copy(Bitmap.Config.ARGB_8888, true)
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
                    saveImageToMediaStore(
                        modifiedImage,
                        "ModifiedImage_${System.currentTimeMillis()}.jpg"
                    )
                } else {
                    Log.e(
                        "MyApp",
                        "No se pudo crear el Bitmap desde el archivo: ${file.absolutePath}"
                    )
                }

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
            Toast.makeText(requireContext(), "Imagen guardada correctamente", Toast.LENGTH_LONG)
                .show()
        } catch (e: IOException) {
            Toast.makeText(requireContext(), "Error al guardar la imagen", Toast.LENGTH_LONG).show()
        }

    }

    private fun openFragment(fragment: Fragment) {
        val transaction: FragmentTransaction =
            requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.contenedor, fragment)
        transaction.commit()
    }

    companion object {
        private const val TAG = "CameraXApp"
        private val request_permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
    }
}