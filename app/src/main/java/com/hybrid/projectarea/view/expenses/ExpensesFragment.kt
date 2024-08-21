package com.hybrid.projectarea.view.expenses

import android.Manifest
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
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
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.hybrid.projectarea.R
import com.hybrid.projectarea.api.ApiService
import com.hybrid.projectarea.api.AuthManager
import com.hybrid.projectarea.databinding.FragmentExpensesBinding
import com.hybrid.projectarea.utils.Alert
import com.hybrid.projectarea.model.ExpenseForm
import com.hybrid.projectarea.utils.HideKeyboard
import com.hybrid.projectarea.model.RequestPermissions
import com.hybrid.projectarea.model.RetrofitClient
import com.hybrid.projectarea.model.TokenAuth
import com.hybrid.projectarea.utils.showDatePickerDialog
import com.hybrid.projectarea.utils.encodeImage
import com.hybrid.projectarea.utils.rotateAndCreateBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

typealias LumaListener = (luma: Double) -> Unit

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
            arrayOf("Arequipa", "Chala", "Moquegua Pint", "Moquegua Pext", "Tacna", "MDD1", "MDD2")
        val adapterZone =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, optionsZone)
        binding.zone.adapter = adapterZone

        val optionsExpense =
            arrayOf("Hospedaje", "Movilidad", "Peaje", "Fletes", "Consumibles", "Otros")
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
            if (RequestPermissions.hasPermissions(requireContext(), request_permissions)) {
                binding.formPreproject.isVisible = false
                binding.cameraPreproject.isVisible = true
                (activity as? AppCompatActivity)?.supportActionBar?.hide()
                startCamera()
            } else {
                requestPermissionLauncherCameraLocation.launch(
                    request_permissions
                )
            }
        }

        binding.options.btnHistory.setOnClickListener {
            openFragment(HistoryExpenseFragment())
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

    private fun openFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.contenedor, fragment)
        transaction.commit()
    }

    private fun send(formData: ExpenseForm) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val token = TokenAuth.getToken(requireContext(), "token")
                val apiService = RetrofitClient.getClient(token).create(ApiService::class.java)
                val authManager = AuthManager(apiService)
                authManager.funExpenseForm(
                    token,
                    formData,
                    object : AuthManager.inExpenseForm {
                        override fun onExpenseFormSuccess() {
                            Alert.alertSuccess(requireContext(), layoutInflater)
                            dataCleaning()
                            binding.send.buttonSend.isEnabled = true
                        }

                        override fun onExpenseFormFailed(errorMessage: String) {
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG)
                                .show()
                            println(errorMessage)
                            binding.send.buttonSend.isEnabled = true
                        }
                    }
                )
            } catch (e: Exception) {

            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
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

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalyzer
                )

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

    private val requestPermissionLauncherCameraLocation = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraPermissionGranted = permissions[Manifest.permission.CAMERA] ?: false

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
    }

    companion object {
        private const val TAG = "CameraXApp"
        private val request_permissions = arrayOf(
            Manifest.permission.CAMERA
        )
    }
}