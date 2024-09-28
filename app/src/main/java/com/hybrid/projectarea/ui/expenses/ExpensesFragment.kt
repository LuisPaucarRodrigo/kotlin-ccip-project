package com.hybrid.projectarea.ui.expenses

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.OrientationEventListener
import android.view.ScaleGestureDetector
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.hybrid.projectarea.R
import com.hybrid.projectarea.api.ApiService
import com.hybrid.projectarea.api.AuthManager
import com.hybrid.projectarea.databinding.FragmentExpensesBinding
import com.hybrid.projectarea.databinding.GalleryOrCameraBinding
import com.hybrid.projectarea.domain.model.ExpenseForm
import com.hybrid.projectarea.utils.Alert
import com.hybrid.projectarea.utils.HideKeyboard
import com.hybrid.projectarea.model.RequestPermissions
import com.hybrid.projectarea.model.RetrofitClient
import com.hybrid.projectarea.model.TokenAuth
import com.hybrid.projectarea.utils.showDatePickerDialog
import com.hybrid.projectarea.utils.encodeImage
import com.hybrid.projectarea.utils.rotateAndCreateBitmap
import com.hybrid.projectarea.ui.DeleteTokenAndCloseSession
import com.hybrid.projectarea.utils.aspectRatio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ExpensesFragment : Fragment() {
    private var _binding: FragmentExpensesBinding? = null
    private val binding get() = _binding!!

    private var bills: String = ""

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var file: File

    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentExpensesBinding.inflate(inflater, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Gastos"
        binding.options.btnExpenses.apply {
            setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.azulccip))
            setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }
        val optionsZone =
            arrayOf("Arequipa", "Chala", "Moquegua", "Tacna", "MDD1", "MDD2")
        val adapterZone =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, optionsZone)
        binding.zone.adapter = adapterZone

        val optionsExpense =
            arrayOf("Hospedaje", "Pasaje Interprovincial", "Peaje","Taxis y Pasajes" ,"Mensajeria", "Consumibles","Bandeos", "Otros")
        val adapterExpense =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, optionsExpense)
        binding.typeExpense.adapter = adapterExpense

        val optionsDocument =
            arrayOf("Efectivo", "Deposito", "Factura", "Boleta", "Voucher de Pago")
        val adapterDocument =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, optionsDocument)
        binding.typeDocument.adapter = adapterDocument

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnDocumentPhoto.setOnClickListener {
            selectionCameraOrGallery()

        }

        binding.options.btnHistory.setOnClickListener {
            openFragment(R.id.to_HistoryExpensesFragment)
        }

        binding.txtDateDocument.setOnClickListener {
            showDatePickerDialog(requireContext()) { selectedDate ->
                val formattedDate = selectedDate.time
                binding.txtDateDocument.setText(dateFormatter.format(formattedDate))
            }
        }

        binding.captureButton.setOnClickListener {
            takePhoto { photo ->
                bills = photo
                binding.btnDocumentPhoto.text = "Imagen Subida"
                (activity as? AppCompatActivity)?.supportActionBar?.show()
            }
        }

        binding.closeCamera.setOnClickListener {
            (activity as? AppCompatActivity)?.supportActionBar?.show()
            binding.formPreproject.isVisible = true
            binding.cameraPreproject.isVisible = false
            stopCamera()
        }

        binding.send.buttonSend.setOnClickListener {
            val formData = collectFormData()
            if (areAllFieldsFilled(formData)) {
                HideKeyboard.hideKeyboard(binding.root)
                binding.send.buttonSend.isEnabled = false
                send(formData)
            } else {
                Toast.makeText(context, "Por favor, llena todos los campos", Toast.LENGTH_LONG)
                    .show()
            }
        }
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun openFragment(id: Int) {
        findNavController().navigate(id)
    }

    private fun send(formData: ExpenseForm) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val token = TokenAuth.getToken(requireContext(), "token")
                val apiService = RetrofitClient.getClient(token)
                val authManager = AuthManager(apiService)
                authManager.funExpenseForm(
                    token,
                    formData,
                    object : AuthManager.inExpenseForm {
                        override fun onExpenseFormSuccess() {
                            lifecycleScope.launch(Dispatchers.Main) {
                                Alert.alertSuccess(requireContext(), layoutInflater)
                                dataCleaning()
                                binding.send.buttonSend.isEnabled = true
                            }
                        }

                        override fun onExpenseFormNoAuthenticated() {
                            DeleteTokenAndCloseSession(this@ExpensesFragment)
                        }

                        override fun onExpenseFormFailed(errorMessage: String) {
                            lifecycleScope.launch(Dispatchers.Main) {
                                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG)
                                    .show()
                                binding.send.buttonSend.isEnabled = true
                            }
                        }
                    }
                )
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Se produjo un error inesperado. Por favor inténtalo de nuevo.",
                        Toast.LENGTH_LONG
                    ).show()
                }
                binding.send.buttonSend.isEnabled = true
            }
        }
    }

    private fun selectionCameraOrGallery(){
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val bottomSheetView = GalleryOrCameraBinding.inflate(layoutInflater)
        val dialogView = bottomSheetView.root

        bottomSheetDialog.setContentView(dialogView)
        bottomSheetDialog.show()

        bottomSheetView.btnGallery.setOnClickListener {
            val apiPermissions = when {
                Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU -> {
                    request_permissions_gallery
                }
                else -> {
                    request_permissions_gallery_api33
                }
            }
            if (RequestPermissions.hasPermissions(requireContext(),apiPermissions)) {
                pickPhotoFromGallery()
            } else {
                requestPermissionLauncherCameraLocation.launch(apiPermissions)
            }
            bottomSheetDialog.dismiss()
        }
        bottomSheetView.btnCamera.setOnClickListener {
            HideKeyboard.hideKeyboard(binding.root)
            if (RequestPermissions.hasPermissions(requireContext(), request_permissions)) {
                binding.formPreproject.isVisible = false
                binding.cameraPreproject.isVisible = true
                startCamera()
            } else {
                requestPermissionLauncherCameraLocation.launch(
                    request_permissions
                )
            }
            bottomSheetDialog.dismiss()
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
            val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, Uri.parse(uri.toString()))
            bills = encodeImage(bitmap)!!
            binding.btnDocumentPhoto.text = "Imagen Subida"
        }
    }

    private fun startCamera() {
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        val screenAspectRatio = aspectRatio()

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .setTargetAspectRatio(screenAspectRatio)
                .build()
                .also {
                    it.setSurfaceProvider(binding.previewView.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .setTargetAspectRatio(screenAspectRatio)
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

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
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

    private fun takePhoto(baseImage: (String) -> Unit) {
        createPhotoFile()
        val imageCapture = imageCapture ?: return
        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    binding.cameraPreproject.isVisible = false
                    binding.formPreproject.isVisible = true
                    baseImage(encodeImage(rotateAndCreateBitmap(file))!!)
                }
            }
        )
    }

    private fun stopCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll() // Desvincula todos los casos de uso de la cámara
        }, ContextCompat.getMainExecutor(requireContext()))

        cameraExecutor.shutdown() // Detener el executor para liberar recursos
    }

    private val requestPermissionLauncherCameraLocation = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraPermissionGranted = permissions[Manifest.permission.CAMERA] ?: false
        val galleryPermissionGranted = when {
            Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU -> permissions[Manifest.permission.READ_MEDIA_IMAGES] ?: false
            else -> permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: false
        }

        if (cameraPermissionGranted) {
            binding.formPreproject.isVisible = false
            binding.cameraPreproject.isVisible = true
            startCamera()
        } else {
            Snackbar.make(
                binding.root,
                getString(R.string.enable_camera_permission),
                Snackbar.LENGTH_LONG
            ).show()

            if (galleryPermissionGranted) {
                pickPhotoFromGallery()
            } else {
                Snackbar.make(
                    binding.root,
                    "Permisos de Galeria no concedidos",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun createPhotoFile() {
        val dir = view?.context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        file = File.createTempFile("IMG_${System.currentTimeMillis()}_", ".png", dir)
    }

    private fun collectFormData(): ExpenseForm {
        return ExpenseForm(
            zone = binding.zone.selectedItem.toString(),
            expense_type = binding.typeExpense.selectedItem.toString(),
            type_doc = binding.typeDocument.selectedItem.toString(),
            ruc = binding.txtRuc.text.toString(),
            doc_number = binding.txtNumberDocument.text.toString(),
            doc_date = binding.txtDateDocument.text.toString(),
            amount = binding.txtAmount.text.toString(),
            description = binding.txtDescription.text.toString(),
            project_id = "",
            photo = bills,
        )
    }

    private fun areAllFieldsFilled(formData: ExpenseForm): Boolean {
        return formData.zone.isNotEmpty() && formData.expense_type.isNotEmpty() &&
                formData.type_doc.isNotEmpty() && formData.ruc.isNotEmpty() &&
                formData.doc_date.isNotEmpty() && formData.amount.isNotEmpty() &&
                formData.description.isNotEmpty()
    }

    private fun dataCleaning() {
        val arraySpinner = listOf(
            binding.zone,
            binding.typeExpense,
            binding.typeDocument
        )
        arraySpinner.forEach { it.setSelection(0) }

        val arrayText = listOf(
            binding.txtRuc,
            binding.txtNumberDocument,
            binding.txtAmount,
            binding.txtDescription,
            binding.txtDateDocument
        )
        arrayText.forEach { it.text.clear() }
        bills = ""
        binding.btnDocumentPhoto.text = "Subir Foto"
    }

    companion object {
        private const val TAG = "CameraXApp"
        private val request_permissions = arrayOf(
            Manifest.permission.CAMERA
        )

        private val request_permissions_gallery = arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES
        )

        private val request_permissions_gallery_api33 = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }
}