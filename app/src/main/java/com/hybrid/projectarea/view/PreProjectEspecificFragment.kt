package com.hybrid.projectarea.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.ColorDrawable
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
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.hybrid.projectarea.R
import com.hybrid.projectarea.api.ApiService
import com.hybrid.projectarea.api.AuthManager
import com.hybrid.projectarea.databinding.FragmentPreProjectEspecificBinding
import com.hybrid.projectarea.databinding.OptionsPhotoCameraBinding
import com.hybrid.projectarea.databinding.SuccessfulRequestBinding
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

class PreProjectEspecificFragment : Fragment() {

    private var _binding:FragmentPreProjectEspecificBinding? = null
    private val binding get() = _binding!!
    private var photoString:String? = null
    private var intent:Intent? = null
    private lateinit var file: File

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
            args.putString("id",requireArguments().getString("id").toString())
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
                    val token = TokenAuth.getToken(requireContext())
                    val apiService = RetrofitClient.getClient(token).create(ApiService::class.java)
                    val authManager = AuthManager(apiService)
                    authManager.preProjectPhoto(token,requireArguments().getString("id").toString(),description,
                        photoString.toString(), object : AuthManager.PreProjectAddPhoto {
                            override fun onPreProjectAddPhotoSuccess() {
                                alertSuccess()
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

    private fun apiRequestPreProject() {
        lifecycleScope.launch {
            try {
                val token = TokenAuth.getToken(requireContext())
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
            if(PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(dialogView.context,Manifest.permission.CAMERA) &&
             PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(dialogView.context,Manifest.permission.ACCESS_COARSE_LOCATION)){
                startForResult.launch(intent)

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
                    photoString = encodeImage(getBitmap())
                    binding.photo.photoPreview.setImageBitmap(getBitmap())
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
        bm.compress(Bitmap.CompressFormat.JPEG, 40, baos)
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
            startForResult.launch(intent)
        } else {
            if (!cameraPermissionGranted) {
                Snackbar.make(binding.root, getString(R.string.enable_camera_permission), Snackbar.LENGTH_LONG).show()
            }
            if (!coarseLocationGranted) {
                Snackbar.make(binding.root, getString(R.string.enable_coarse_location_permission), Snackbar.LENGTH_LONG).show()
            }
        }
    }


    private fun dataCleaning() {
        binding.addDescription.text.clear()
        binding.photo.photoPreview.setImageResource(0)
        photoString = null
    }
}