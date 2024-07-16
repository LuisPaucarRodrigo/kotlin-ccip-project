package com.hybrid.projectarea.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
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
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.hybrid.projectarea.R
import com.hybrid.projectarea.api.ApiService
import com.hybrid.projectarea.api.AuthManager
import com.hybrid.projectarea.databinding.FragmentPreProjectEspecificBinding
import com.hybrid.projectarea.model.CodePhotoDescription
import com.hybrid.projectarea.model.DateTimeLocationManager
import com.hybrid.projectarea.model.ImageOverlay
import com.hybrid.projectarea.model.RetrofitClient
import com.hybrid.projectarea.model.TokenAuth
import com.hybrid.projectarea.model.HideKeyboard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File

import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.tasks.Task

import android.location.LocationManager
import com.hybrid.projectarea.model.Alert
import com.hybrid.projectarea.model.PhotoRequest

class PreProjectEspecificFragment : Fragment() {

    private var _binding:FragmentPreProjectEspecificBinding? = null
    private val binding get() = _binding!!
    private var photoString:String? = null
    private var intent:Intent? = null
    private lateinit var file: File
    private var latitude:Double? = null
    private var longitude:Double? = null
    private lateinit var preprojectCodeId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preprojectCodeId = requireArguments().getString("id").toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPreProjectEspecificBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        apiRequestPreProject()

        binding.imagesCode.setOnClickListener {
            val conceptFragment = RegisterPhotoFragment()
            val args = Bundle()
            args.putString("id",preprojectCodeId)
            conceptFragment.arguments = args

            val transition: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            transition.replace(R.id.contenedor, conceptFragment)
                .addToBackStack(null)
                .commit()
        }

        binding.photo.addPhoto.setOnClickListener{
            selectionCameraAndPhoto()
        }

        binding.send.buttonSend.setOnClickListener{
            HideKeyboard.hideKeyboard(binding.root)
            binding.send.buttonSend.isEnabled = false
            send(binding.addDescription.text.toString())
        }
    }

    private fun send(description: String){
        if(description.isNotBlank() && photoString != null){
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val token = TokenAuth.getToken(requireContext(),"token")
                    val apiService = RetrofitClient.getClient(token).create(ApiService::class.java)
                    val authManager = AuthManager(apiService)
                    val formImageProject = PhotoRequest(preprojectCodeId,description,photoString.toString(),latitude.toString(),longitude.toString())
                    authManager.preProjectPhoto(token,formImageProject, object : AuthManager.PreProjectAddPhoto {
                            override fun onPreProjectAddPhotoSuccess() {
                                Alert.alertSuccess(requireContext(),layoutInflater)
                                dataCleaning()
                                binding.send.buttonSend.isEnabled = true
                            }
                            override fun onPreProjectAddPhotoFailed(errorMessage: String) {
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
            Snackbar.make(binding.root,"Complete los campos",Snackbar.LENGTH_LONG).show()
            binding.send.buttonSend.isEnabled = true
        }
    }

    private fun apiRequestPreProject() {
        lifecycleScope.launch {
            try {
                val token = TokenAuth.getToken(requireContext(),"token")
                val apiService = withContext(Dispatchers.IO) {
                    RetrofitClient.getClient(token).create(ApiService::class.java)
                }

                val authManager = AuthManager(apiService)
                authManager.codephotospecific(token,requireArguments().getString("id").toString(), object : AuthManager.inCodePhotoDescription {
                    override fun onCodePhotoDescriptionPreProjectSuccess(response: CodePhotoDescription) {
                        binding.codePreproject.text = response.codePreproject
                        binding.codePhoto.text = response.code
                        binding.codeStatus.text = response.status
                        binding.codeDescription.text = response.description
                    }
                    override fun onCodePhotoDesrriptionPreProjectFailed() {
                        Toast.makeText(requireContext(),getString(R.string.check_connection), Toast.LENGTH_LONG).show()
                    }
                })
            } catch (e: Exception) {
                Toast.makeText(requireContext(),"Intento Fallido de exception", Toast.LENGTH_LONG).show()

            }
        }
    }

    private fun selectionCameraAndPhoto() {
        intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE).also {
            requireContext().let { it1 ->
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
        if(allPermissionGranted()){
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
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
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
                    latitude = location.latitude
                    longitude = location.longitude
                    val mutableBitmap = getBitmap().copy(Bitmap.Config.ARGB_8888, true)
                    val logoBitmap = ContextCompat.getDrawable(requireContext(),R.drawable.logo_ccip_white)?.toBitmap()
                    val modifiedImage = ImageOverlay.overlayTextOnImage(mutableBitmap,logoBitmap!! ,currentDateTime, latitude!!, longitude!!)
                    photoString = encodeImage(modifiedImage)
                    binding.photo.photoPreview.setImageBitmap(modifiedImage)
                } else {
                    requestGPSEnable()
                }
            }
        }
    }

    private fun encodeImage(bm: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 40, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    private fun getBitmap(): Bitmap = BitmapFactory.decodeFile(file.toString())

    private val requestPermissionLauncherCameraLocation = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraPermissionGranted = permissions[Manifest.permission.CAMERA] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false

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
        binding.addDescription.text.clear()
        binding.photo.photoPreview.setImageResource(0)
        photoString = null
    }

    private fun allPermissionGranted () = request_permissions.all{ permission ->
        ContextCompat.checkSelfPermission(requireContext(),permission) == PackageManager.PERMISSION_GRANTED
    }

    companion object{
        private val request_permissions = arrayOf(Manifest.permission.CAMERA,Manifest.permission.ACCESS_FINE_LOCATION)
    }
}