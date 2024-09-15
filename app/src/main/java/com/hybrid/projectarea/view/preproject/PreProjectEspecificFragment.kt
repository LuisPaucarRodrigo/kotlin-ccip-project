package com.hybrid.projectarea.view.preproject

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.LocationManager
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.OrientationEventListener
import android.view.ScaleGestureDetector
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.hybrid.projectarea.R
import com.hybrid.projectarea.api.ApiService
import com.hybrid.projectarea.api.AuthManager
import com.hybrid.projectarea.databinding.FragmentPreProjectEspecificBinding
import com.hybrid.projectarea.databinding.PhotoCodeBinding
import com.hybrid.projectarea.model.CodePhotoDescription
import com.hybrid.projectarea.model.DateTimeLocationManager
import com.hybrid.projectarea.model.ImageOverlay
import com.hybrid.projectarea.model.Images
import com.hybrid.projectarea.model.PhotoRequest
import com.hybrid.projectarea.model.RequestPermissions
import com.hybrid.projectarea.model.RetrofitClient
import com.hybrid.projectarea.model.TokenAuth
import com.hybrid.projectarea.utils.Alert
import com.hybrid.projectarea.utils.HideKeyboard
import com.hybrid.projectarea.utils.encodeImage
import com.hybrid.projectarea.utils.rotateAndCreateBitmap
import com.hybrid.projectarea.view.DeleteTokenAndCloseSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class PreProjectEspecificFragment : Fragment() {

    private var _binding: FragmentPreProjectEspecificBinding? = null
    private val binding get() = _binding!!
    private var photoString: String = ""

    //    private var intent: Intent? = null
    private lateinit var file: File
    private var latitude: Double? = null
    private var longitude: Double? = null
    private lateinit var preprojectCodeId: String

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preprojectCodeId = requireArguments().getString("code_id").toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPreProjectEspecificBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        apiRequestPreProject()

        binding.imagesCode.setOnClickListener {
            val conceptFragment = RegisterPhotoFragment()
            val args = Bundle()
            args.putString("id", preprojectCodeId)
            conceptFragment.arguments = args

            val transition: FragmentTransaction =
                requireActivity().supportFragmentManager.beginTransaction()
            transition.replace(R.id.contenedor, conceptFragment)
                .addToBackStack(null)
                .commit()
        }

        binding.photo.addPhoto.setOnClickListener {
            selectionCamera()
        }

        binding.send.buttonSend.setOnClickListener {
            HideKeyboard.hideKeyboard(binding.root)
            val formData = collectFormData()
            if (areAllFieldsFilled(formData)){
                binding.send.buttonSend.isEnabled = false
                send(formData)
            }else{
                Toast.makeText(context, "Por favor, llena todos los campos", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.captureButton.setOnClickListener {
            takePhoto()
        }
        binding.closeCamera.setOnClickListener {
            binding.formPreproject.isVisible = true
            binding.cameraPreproject.isVisible = false
            (activity as? AppCompatActivity)?.supportActionBar?.show()
            stopCamera()
        }
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun send(formData: PhotoRequest) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val token = TokenAuth.getToken(requireContext(), "token")
                val apiService = RetrofitClient.getClient(token).create(ApiService::class.java)
                val authManager = AuthManager(apiService)
                authManager.preProjectPhoto(
                    token,
                    formData,
                    object : AuthManager.PreProjectAddPhoto {
                        override fun onPreProjectAddPhotoSuccess() {
                            Alert.alertSuccess(requireContext(), layoutInflater)
                            dataCleaning()
                            binding.send.buttonSend.isEnabled = true
                        }

                        override fun onPreProjectAddPhotoNoAuthenticated() {
                            DeleteTokenAndCloseSession(this@PreProjectEspecificFragment)
                        }

                        override fun onPreProjectAddPhotoFailed(errorMessage: String) {
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG)
                                .show()
                            binding.send.buttonSend.isEnabled = true
                        }
                    }
                )
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Se produjo un error inesperado. Por favor inténtalo de nuevo.", Toast.LENGTH_LONG).show()
                    binding.send.buttonSend.isEnabled = true
                }
            }
        }
    }

    private fun apiRequestPreProject() {
        val arrayList = ArrayList<Images>()
        binding.recyclerImages.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val token = TokenAuth.getToken(requireContext(), "token")
                val apiService = RetrofitClient.getClient(token).create(ApiService::class.java)
                val authManager = AuthManager(apiService)
                authManager.codephotospecific(
                    token,
                    preprojectCodeId,
                    object : AuthManager.inCodePhotoDescription {
                        override fun onCodePhotoDescriptionPreProjectSuccess(response: CodePhotoDescription) {
                            binding.codePreproject.text = response.codePreproject
                            binding.codePhoto.text = response.code
                            binding.codeStatus.text = response.status
                            binding.codeDescription.text = response.description
                            val adapter = AdapterReferenceImage(
                                response.images,
                                object : AdapterReferenceImage.OnItemClickListener {
                                    override fun onItemClick(position: Int) {
                                        val item = response.images[position]
                                        showImageDialog(item.url)
                                    }
                                }
                            )
                            binding.recyclerImages.adapter = adapter
                        }

                        override fun onCodePhotoDescriptionPreProjectNoAuthenticated() {
                            DeleteTokenAndCloseSession(this@PreProjectEspecificFragment)
                        }

                        override fun onCodePhotoDesrriptionPreProjectFailed() {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.check_connection),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    })
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Se produjo un error inesperado.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Función para mostrar la imagen ampliada en un Dialog
    private fun showImageDialog(imageUrl: String) {
        val builder = AlertDialog.Builder(requireActivity())
        val alertDialogBinding = PhotoCodeBinding.inflate(layoutInflater)
        val dialogView = alertDialogBinding.root
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        // Cargar la imagen ampliada
        Glide.with(requireContext())
            .load(imageUrl)
            .placeholder(R.drawable.baseline_downloading_24)
            .error(R.drawable.baseline_error_24)
            .into(alertDialogBinding.photo)

    }

    private fun selectionCamera() {
        HideKeyboard.hideKeyboard(binding.root)
        if (RequestPermissions.hasPermissions(requireContext(), request_permissions)) {
            checkGpsStatus().addOnSuccessListener {
                binding.formPreproject.isVisible = false
                binding.cameraPreproject.isVisible = true
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

            val orientationEventListener = object : OrientationEventListener(requireContext()) {
                override fun onOrientationChanged(orientation : Int) {
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

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                val camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalyzer
                )

                // Pinch-to-zoom setup
                val cameraControl = camera.cameraControl
                val cameraInfo = camera.cameraInfo

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
                    true
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
                    binding.cameraPreproject.isVisible = false
                    binding.formPreproject.isVisible = true
                    (activity as? AppCompatActivity)?.supportActionBar?.show()
                    stopCamera()
                    image()

                }
            }
        )
    }

    private fun createPhotoFile() {
        val dir = view?.context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        file = File.createTempFile("IMG_${System.currentTimeMillis()}_", ".png", dir)
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
                    longitude!!,
                    binding.codePreproject.text.toString()
                )
                photoString = encodeImage(modifiedImage)!!
                binding.photo.photoPreview.setImageBitmap(modifiedImage)
            } else {
                requestGPSEnable()
            }
        }
    }

    private val requestPermissionLauncherCameraLocation = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraPermissionGranted = permissions[Manifest.permission.CAMERA] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false

        if (cameraPermissionGranted && coarseLocationGranted) {
            checkGpsStatus().addOnSuccessListener {
                binding.formPreproject.isVisible = false
                binding.cameraPreproject.isVisible = true
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

    private fun collectFormData(): PhotoRequest {
        return PhotoRequest(
            id = preprojectCodeId,
            description = binding.addDescription.text.toString(),
            photo = photoString,
            latitude = latitude.toString(),
            longitude = longitude.toString(),
        )
    }

    private fun areAllFieldsFilled(formData: PhotoRequest): Boolean {
        return formData.id.isNotEmpty() && formData.description.isNotEmpty() &&
                formData.photo.isNotEmpty() && formData.longitude!!.isNotEmpty() &&
                formData.latitude!!.isNotEmpty()
    }

    private fun dataCleaning() {
        binding.addDescription.text.clear()
        binding.photo.photoPreview.setImageResource(0)
        photoString = ""
    }

    companion object {
        private const val TAG = "CameraXApp"
        private val request_permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }
}