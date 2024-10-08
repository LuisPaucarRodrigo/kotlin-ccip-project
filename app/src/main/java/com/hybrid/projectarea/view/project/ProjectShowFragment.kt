package com.hybrid.projectarea.view.project

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.ColorDrawable
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.hybrid.projectarea.R
import com.hybrid.projectarea.api.ApiService
import com.hybrid.projectarea.api.AuthManager
import com.hybrid.projectarea.databinding.FragmentProjectShowBinding
import com.hybrid.projectarea.databinding.OptionsPhotoCameraBinding
import com.hybrid.projectarea.databinding.SuccessfulRequestBinding
import com.hybrid.projectarea.model.DateTimeLocationManager
import com.hybrid.projectarea.model.ImageOverlay
import com.hybrid.projectarea.model.ProjectFind
import com.hybrid.projectarea.model.RetrofitClient
import com.hybrid.projectarea.model.TokenAuth
import com.hybrid.projectarea.utils.HideKeyboard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File

class ProjectShowFragment : Fragment() {
    private var _binding:FragmentProjectShowBinding? = null
    private val binding get() = _binding!!
    private var photoString:String? = null
    private var intent:Intent? = null
    private lateinit var file: File

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        _binding = FragmentProjectShowBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        apiRequestProject()
        binding.photo.addPhoto.setOnClickListener{
            selectionCameraAndPhoto()
        }

        binding.send.buttonSend.setOnClickListener{
            HideKeyboard.hideKeyboard(binding.root)
            binding.send.buttonSend.isEnabled = false
            send(binding.addescription.text.toString())
        }
    }

    private fun send(description: String){
        if(description.isNotBlank() && photoString != null){
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val token = TokenAuth.getToken(requireContext(),"token")
                    val apiService = RetrofitClient.getClient(token).create(ApiService::class.java)
                    val authManager = AuthManager(apiService)
                    authManager.projectPhoto(token,requireArguments().getString("id").toString(),description,
                        photoString.toString(), object : AuthManager.ProjectStorePhoto {
                            override fun onProjectAddPhotoSuccess() {
                                alertSuccess()
                                dataCleaning()
                                binding.send.buttonSend.isEnabled = true
                            }
                            override fun onProjectAddPhotoFailed(errorMessage: String) {
                                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
                                binding.send.buttonSend.isEnabled = true
                            }
                        }
                    )
                } catch (e: Exception) {
                    // Manejar errores
                }
            }
        }else{
            Snackbar.make(binding.root,"Complete los campos", Snackbar.LENGTH_LONG).show()
        }
    }

    private fun alertSuccess() {
        val builder = AlertDialog.Builder(requireContext())
        val alertDialogBinding = SuccessfulRequestBinding.inflate(layoutInflater)
        val dialogView = alertDialogBinding.root
        builder.setView(dialogView)
        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        Handler(Looper.getMainLooper()).postDelayed({
            dialog.dismiss()
        },1500)
    }

    private fun apiRequestProject() {
        lifecycleScope.launch {
            try {
                val token = TokenAuth.getToken(requireContext(),"token")
                val apiService = withContext(Dispatchers.IO) {
                    RetrofitClient.getClient(token).create(ApiService::class.java)
                }

                val authManager = AuthManager(apiService)
                authManager.projectshow(token,requireArguments().getString("id").toString(), object : AuthManager.ProjectShow {
                    override fun onProjectSpecificSuccess(response: ProjectFind) {
                        binding.code.text = response.code
                        binding.description.text = response.description
                    }
                    override fun onProjectSpecificFailed() {
                        Toast.makeText(requireContext(),getString(R.string.check_connection), Toast.LENGTH_LONG).show()
                    }
                })
            } catch (e: Exception) {
                Toast.makeText(requireContext(),"Intento Fallido de exception", Toast.LENGTH_LONG).show()

            }
        }
    }

    private fun selectionCameraAndPhoto() {
        val builder = AlertDialog.Builder(this.requireActivity())
        val alertDialogBinding = OptionsPhotoCameraBinding.inflate(layoutInflater)
        val dialogView = alertDialogBinding.root
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        alertDialogBinding.btnGallery.setOnClickListener {
            dialog.dismiss()
            val checkpermission =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                    Manifest.permission.READ_MEDIA_IMAGES
                }else{
                    Manifest.permission.READ_EXTERNAL_STORAGE
                }
            if (ContextCompat.checkSelfPermission(dialogView.context,checkpermission) == PackageManager.PERMISSION_GRANTED){
                pickPhotoFromGallery()
            }else{
                requestPermissionLauncher.launch(checkpermission)
            }
        }

        alertDialogBinding.btnCamera.setOnClickListener {
            dialog.dismiss()
            intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE).also {
                dialogView.context?.let { it1 ->
                    it.resolveActivity(it1.packageManager).also { _ ->
                        createPhotoFile()
                        val photoUri: Uri =
                            FileProvider.getUriForFile(
                                requireView().context,
                                requireView().context.packageName + ".fileprovider", file
                            )
                        it.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                    }
                }
            }
            if(PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(dialogView.context,
                    Manifest.permission.CAMERA) &&
                PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(dialogView.context,
                    Manifest.permission.ACCESS_COARSE_LOCATION)){
                checkGpsStatus().addOnSuccessListener {
                    startForResult.launch(intent)
                }
                checkGpsStatus().addOnFailureListener {
                    requestGPSEnable()
                }
            }else{
                requestPermissionLauncherCameraLocation.launch(
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    private fun pickPhotoFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startForActivityGallery.launch(intent)
    }

    private val startForActivityGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){result ->
        if (result.resultCode == Activity.RESULT_OK){
            val uri = result.data?.data
            val source = ImageDecoder.createSource(requireActivity().contentResolver, uri!!)
            val bitmap = ImageDecoder.decodeBitmap(source)

            photoString = encodeImage(bitmap)

            binding.photo.photoPreview.setImageBitmap(bitmap)
        }
    }


    private fun createPhotoFile() {
        val dir = view?.context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        file = File.createTempFile("IMG_${System.currentTimeMillis()}_", ".png",dir)
    }

    private val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){result ->
        if (result.resultCode == Activity.RESULT_OK){
            val currentDateTime = DateTimeLocationManager.getCurrentDateTime()
            DateTimeLocationManager.getCurrentLocation(requireContext()) { location ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    val mutableBitmap = getBitmap().copy(Bitmap.Config.ARGB_8888, true)
                    val logoBitmap = ContextCompat.getDrawable(requireContext(),R.drawable.logo_ccip_white)?.toBitmap()
                    val logoSyze = Bitmap.createScaledBitmap(logoBitmap!!, 90, 60, false)
                    val modifiedImage = ImageOverlay.overlayTextOnImage(mutableBitmap,logoSyze ,currentDateTime, latitude, longitude)

                    photoString = encodeImage(modifiedImage)
                    binding.photo.photoPreview.setImageBitmap(modifiedImage)
                } else {
                    requestGPSEnable()
                }
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){isGranted ->
        if (isGranted){
            pickPhotoFromGallery()
        }else{
            Snackbar.make(binding.root, getString(R.string.enable_galery_permission), Snackbar.LENGTH_LONG).show()
        }
    }

    private fun encodeImage(bm: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 25, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    private fun getBitmap(): Bitmap = BitmapFactory.decodeFile(file.toString())

//    private val requestPermissionLaunchercamera = registerForActivityResult(
//        ActivityResultContracts.RequestPermission()
//    ){isGranted ->
//        if (isGranted){
//            startForResult.launch(intent)
//        }else{
//            Snackbar.make(binding.root, getString(R.string.enable_camera_permission), Snackbar.LENGTH_LONG).show()
//        }
//    }

    private val requestPermissionLauncherCameraLocation = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraPermissionGranted = permissions[Manifest.permission.CAMERA] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (cameraPermissionGranted && coarseLocationGranted) {
            checkGpsStatus().addOnSuccessListener {
                startForResult.launch(intent)
            }
            checkGpsStatus().addOnFailureListener {
                requestGPSEnable()
            }
        } else {
            if (!cameraPermissionGranted) {
                Snackbar.make(binding.root, getString(R.string.enable_camera_permission), Snackbar.LENGTH_LONG).show()
            }
            if (!coarseLocationGranted) {
                Snackbar.make(binding.root, getString(R.string.enable_coarse_location_permission), Snackbar.LENGTH_LONG).show()
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
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage("El GPS está desactivado, ¿quieres activarlo?")
                .setCancelable(false)
                .setPositiveButton("Sí") { dialog, id ->
                    requireContext().startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                .setNegativeButton("No") { dialog, id ->
                    dialog.cancel()
                }
            val alert = builder.create()
            alert.show()
        }
    }

    private fun dataCleaning() {
        binding.addescription.text.clear()
        binding.photo.photoPreview.setImageResource(0)
        photoString = null
    }
}