package com.hybrid.projectarea.view.checklist

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
import com.hybrid.projectarea.databinding.FragmentMobileUnitChecklistBinding
import com.hybrid.projectarea.utils.Alert
import com.hybrid.projectarea.utils.HideKeyboard
import com.hybrid.projectarea.model.RequestPermissions
import com.hybrid.projectarea.model.RetrofitClient
import com.hybrid.projectarea.model.TokenAuth
import com.hybrid.projectarea.model.checkListMobile
import com.hybrid.projectarea.utils.encodeImage
import com.hybrid.projectarea.utils.rotateAndCreateBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

typealias LumaListener = (luma: Double) -> Unit

class MobileUnitChecklistFragment : Fragment() {
    private var _binding: FragmentMobileUnitChecklistBinding? = null
    private val binding get() = _binding!!

    private var front: String = ""
    private var leftSide: String = ""
    private var rightSide: String = ""
    private var interior: String = ""
    private var rearLeftTire: String = ""
    private var rearRightTire: String = ""
    private var frontRightTire: String = ""
    private var frontLeftTire: String = ""

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var file: File
    private var identifierBtn: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMobileUnitChecklistBinding.inflate(inflater, container, false)

        binding.scrollChecklist.checkListMobile.apply{
            setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.azulccip))
            setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }

        val optionsDocMovil = arrayOf("Vigente", "Vencido")
        val optionsZone =
            arrayOf("Arequipa", "Chala", "Moquegua Pint", "Moquegua Pext", "Tacna", "MDD1", "MDD2")
        val optionsReasons = listOf("Revision Periódica", "Cambio de Personal")
        val adapterZone =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, optionsZone)

        val adapterReason =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, optionsReasons)
        binding.spinnerReason.adapter = adapterReason
        binding.spinnerZone.adapter = adapterZone

        val arrayBindingDoc = listOf(
            binding.spinnerCirculacion, binding.spinnerTecnica, binding.spinnerSoat
        )

        arrayBindingDoc.forEach { spinner ->
            spinner.adapter = ArrayAdapter(
                requireContext(), android.R.layout.simple_list_item_1, optionsDocMovil
            )
        }

        val optionsStateMovil = arrayOf("Bueno", "Regular", "Malo", "Solicitud")

        val arrayBindingStateMovil = listOf(
            binding.spinnerStateBocina,
            binding.spinnerStateFrenos,
            binding.spinnerStateLucesAltasBajas,
            binding.spinnerStateLucesIntermitentes,
            binding.spinnerStateDireccionales,
            binding.spinnerStateRetrovisores,
            binding.spinnerStateNeumatico,
            binding.spinnerStateParachoques,
            binding.spinnerMarcadorTemperatura,
            binding.spinnerMarcadorAceite,
            binding.spinnermarcadorfuel,
            binding.spinnerAseovehiculo,
            binding.spinnerstatepuertas,
            binding.spinnerstateparabrisas,
            binding.spinnerstatemotor,
            binding.spinnerstatebateria,

            binding.spinnerExtintor,
            binding.spinnerBotiquin,
            binding.spinnerConos,
            binding.spinnerGata,
            binding.spinnerNeumatico,
            binding.spinnerCableRemolque,
            binding.spinnerCableBateria,
            binding.spinnerReflejante,
            binding.spinnerKit,
            binding.spinnerAlarma,
            binding.spinnerTacos,
            binding.spinnerPortaEscalera,
            binding.spinnerPlacaLateral,
        )

        arrayBindingStateMovil.forEach { spinner ->
            spinner.adapter = ArrayAdapter(
                requireContext(), android.R.layout.simple_list_item_1, optionsStateMovil
            )
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.scrollChecklist.checkListDay.setOnClickListener {
            openFragment(DayCheckListFragment())
        }

        binding.scrollChecklist.checkListEpps.setOnClickListener {
            openFragment(EppsCheckListFragment())
        }

        binding.scrollChecklist.checkListTools.setOnClickListener {
            openFragment(ToolsCheckListFragment())
        }

        binding.scrollChecklist.checkListHistory.setOnClickListener {
            openFragment(ChecklistHistoryFragment())
        }

        binding.btnStatePhotoFront.setOnClickListener {
            selectionCameraAndPhoto()
            identifierBtn = "front"
        }

        binding.btnStatePhotoLeft.setOnClickListener {
            selectionCameraAndPhoto()
            identifierBtn = "leftSide"
        }

        binding.btnStatePhotoRight.setOnClickListener {
            selectionCameraAndPhoto()
            identifierBtn = "rightSide"
        }

        binding.btnStateInterior.setOnClickListener {
            selectionCameraAndPhoto()
            identifierBtn = "interior"
        }

        binding.btnRearLeftTire.setOnClickListener {
            selectionCameraAndPhoto()
            identifierBtn = "rearLeftTire"
        }

        binding.btnRearRightTire.setOnClickListener {
            selectionCameraAndPhoto()
            identifierBtn = "rearRightTire"
        }

        binding.btnFrontRightTire.setOnClickListener {
            selectionCameraAndPhoto()
            identifierBtn = "frontRightTire"
        }

        binding.btnFrontLeftTire.setOnClickListener {
            selectionCameraAndPhoto()
            identifierBtn = "frontLeftTire"
        }

        binding.captureButton.setOnClickListener {
            takePhoto { photo ->
                when(identifierBtn){
                    "front" -> {
                        front = photo
                        binding.btnStatePhotoFront.text = "Imagen Subida"
                    }
                    "leftSide" -> {
                        leftSide = photo
                        binding.btnStatePhotoLeft.text = "Imagen Subida"
                    }
                    "rightSide" -> {
                        rightSide = photo
                        binding.btnStatePhotoRight.text = "Imagen Subida"
                    }
                    "interior" -> {
                        interior = photo
                        binding.btnStateInterior.text = "Imagen Subida"
                    }
                    "rearLeftTire" -> {
                        rearLeftTire = photo
                        binding.btnRearLeftTire.text = "Imagen Subida"
                    }
                    "rearRightTire" -> {
                        rearRightTire = photo
                        binding.btnRearRightTire.text = "Imagen Subida"
                    }
                    "frontRightTire" -> {
                        frontRightTire = photo
                        binding.btnFrontRightTire.text = "Imagen Subida"
                    }
                    "frontLeftTire" -> {
                        frontLeftTire = photo
                        binding.btnFrontLeftTire.text = "Imagen Subida"
                    }
                }

            }
        }

        binding.closeCamera.setOnClickListener {
            binding.formPreproject.isVisible = true
            binding.cameraPreproject.isVisible = false
            (activity as? AppCompatActivity)?.supportActionBar?.show()
            stopCamera()
        }

        binding.send.buttonSend.setOnClickListener {
            val ckeckListMovil = collectVehicleFormData()
            if (areAllFieldsFilled(ckeckListMovil)) {
                HideKeyboard.hideKeyboard(binding.root)
                binding.send.buttonSend.isEnabled = false
                sendCheckListMovil(ckeckListMovil)
            } else {
                Toast.makeText(context, "Por favor, llena todos los campos", Toast.LENGTH_LONG)
                    .show()
            }
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun sendCheckListMovil(checkListMobile: checkListMobile) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val token = TokenAuth.getToken(requireContext(), "token")
                val apiService = RetrofitClient.getClient(token).create(ApiService::class.java)
                val authManager = AuthManager(apiService)
                authManager.funCheckListMobile(
                    token,
                    checkListMobile,
                    object : AuthManager.inCheckListMovil {
                        override fun onStoreCheckListMobileSuccess() {
                            Alert.alertSuccess(requireContext(), layoutInflater)
                            dataCleaning()
                            binding.send.buttonSend.isEnabled = true
                        }

                        override fun onStoreCheckListMobileFailed(errorMessage: String) {
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG)
                                .show()
                            binding.send.buttonSend.isEnabled = true
                        }
                    }
                )
            } catch (e: Exception) {

            }
        }
    }

    private fun selectionCameraAndPhoto() {
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

    private fun stopCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll() // Desvincula todos los casos de uso de la cámara
        }, ContextCompat.getMainExecutor(requireContext()))

        cameraExecutor.shutdown() // Detener el executor para liberar recursos
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
                    (activity as? AppCompatActivity)?.supportActionBar?.show()
                    baseImage(encodeImage(rotateAndCreateBitmap(file))!!)
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

    private fun openFragment(fragment: Fragment) {
        val transaction: FragmentTransaction =
            requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.contenedor, fragment)
        transaction.commit()
    }

    private fun collectVehicleFormData(): checkListMobile {
        return checkListMobile(
            reason = binding.spinnerReason.selectedItem.toString(),
            additionalEmployees = binding.txtAddiotionalEmployees.text.toString(),
            zone = binding.spinnerZone.selectedItem.toString(),
            km = binding.txtKM.text.toString(),
            plate = binding.txtCheckCarDocPlaca.text.toString(),
            circulation = binding.spinnerCirculacion.selectedItem.toString(),
            technique = binding.spinnerTecnica.selectedItem.toString(),
            soat = binding.spinnerSoat.selectedItem.toString(),
            hornState = binding.spinnerStateBocina.selectedItem.toString(),
            brakesState = binding.spinnerStateFrenos.selectedItem.toString(),
            headlightsState = binding.spinnerStateLucesAltasBajas.selectedItem.toString(),
            intermitentlightState = binding.spinnerStateLucesIntermitentes.selectedItem.toString(),
            indicatorsState = binding.spinnerStateDireccionales.selectedItem.toString(),
            mirrorsState = binding.spinnerStateRetrovisores.selectedItem.toString(),
            tiresState = binding.spinnerStateNeumatico.selectedItem.toString(),
            bumpersState = binding.spinnerStateParachoques.selectedItem.toString(),
            temperatureGauge = binding.spinnerMarcadorTemperatura.selectedItem.toString(),
            oilGauge = binding.spinnerMarcadorAceite.selectedItem.toString(),
            fuelGauge = binding.spinnermarcadorfuel.selectedItem.toString(),
            vehicleCleanliness = binding.spinnerAseovehiculo.selectedItem.toString(),
            doorsState = binding.spinnerstatepuertas.selectedItem.toString(),
            windshieldState = binding.spinnerstateparabrisas.selectedItem.toString(),
            engineState = binding.spinnerstatemotor.selectedItem.toString(),
            batteryState = binding.spinnerstatebateria.selectedItem.toString(),
            extinguisher = binding.spinnerExtintor.selectedItem.toString(),
            firstAidKit = binding.spinnerBotiquin.selectedItem.toString(),
            cones = binding.spinnerConos.selectedItem.toString(),
            jack = binding.spinnerGata.selectedItem.toString(),
            spareTire = binding.spinnerNeumatico.selectedItem.toString(),
            towCable = binding.spinnerCableRemolque.selectedItem.toString(),
            batteryCable = binding.spinnerCableBateria.selectedItem.toString(),
            reflector = binding.spinnerReflejante.selectedItem.toString(),
            emergencyKit = binding.spinnerKit.selectedItem.toString(),
            alarm = binding.spinnerAlarma.selectedItem.toString(),
            chocks = binding.spinnerTacos.selectedItem.toString(),
            ladderHolder = binding.spinnerPortaEscalera.selectedItem.toString(),
            sidePlate = binding.spinnerPlacaLateral.selectedItem.toString(),
            observation = binding.txtObservation.text.toString(),
            front = front,
            leftSide = leftSide,
            rightSide = rightSide,
            interior = interior,
            rearLeftTire = rearLeftTire,
            rearRightTire = rearRightTire,
            frontRightTire = frontRightTire,
            frontLeftTire = frontLeftTire,
        )
    }

    private fun areAllFieldsFilled(formData: checkListMobile): Boolean {
        return formData.reason.isNotEmpty() && formData.zone.isNotEmpty() && formData.km.isNotEmpty() && formData.plate.isNotEmpty() &&
                formData.circulation.isNotEmpty() && formData.technique.isNotEmpty() && formData.soat.isNotEmpty() &&
                formData.hornState.isNotEmpty() && formData.brakesState.isNotEmpty() && formData.headlightsState.isNotEmpty() && formData.intermitentlightState.isNotEmpty() &&
                formData.indicatorsState.isNotEmpty() && formData.mirrorsState.isNotEmpty() && formData.tiresState.isNotEmpty() &&
                formData.bumpersState.isNotEmpty() && formData.temperatureGauge.isNotEmpty() && formData.oilGauge.isNotEmpty() &&
                formData.fuelGauge.isNotEmpty() && formData.vehicleCleanliness.isNotEmpty() && formData.doorsState.isNotEmpty() &&
                formData.windshieldState.isNotEmpty() && formData.engineState.isNotEmpty() && formData.batteryState.isNotEmpty() &&
                formData.extinguisher.isNotEmpty() && formData.firstAidKit.isNotEmpty() && formData.cones.isNotEmpty() &&
                formData.jack.isNotEmpty() && formData.spareTire.isNotEmpty() && formData.towCable.isNotEmpty() &&
                formData.batteryCable.isNotEmpty() && formData.reflector.isNotEmpty() && formData.emergencyKit.isNotEmpty() &&
                formData.alarm.isNotEmpty() && formData.chocks.isNotEmpty() && formData.ladderHolder.isNotEmpty() &&
                formData.sidePlate.isNotEmpty() && formData.front.isNotEmpty() && formData.leftSide.isNotEmpty() &&
                formData.rightSide.isNotEmpty() && formData.interior.isNotEmpty() && formData.rearLeftTire.isNotEmpty() &&
                formData.rearRightTire.isNotEmpty() && formData.frontRightTire.isNotEmpty() && formData.frontLeftTire.isNotEmpty()
    }

    private fun dataCleaning() {
        val arrayBindingStateMovil = listOf(
            binding.spinnerReason,
            binding.spinnerCirculacion, binding.spinnerTecnica, binding.spinnerSoat,
            binding.spinnerStateBocina,
            binding.spinnerStateFrenos,
            binding.spinnerStateLucesAltasBajas,
            binding.spinnerStateLucesIntermitentes,
            binding.spinnerStateDireccionales,
            binding.spinnerStateRetrovisores,
            binding.spinnerStateNeumatico,
            binding.spinnerStateParachoques,
            binding.spinnerMarcadorTemperatura,
            binding.spinnerMarcadorAceite,
            binding.spinnermarcadorfuel,
            binding.spinnerAseovehiculo,
            binding.spinnerstatepuertas,
            binding.spinnerstateparabrisas,
            binding.spinnerstatemotor,
            binding.spinnerstatebateria,

            binding.spinnerExtintor,
            binding.spinnerBotiquin,
            binding.spinnerConos,
            binding.spinnerGata,
            binding.spinnerNeumatico,
            binding.spinnerCableRemolque,
            binding.spinnerCableBateria,
            binding.spinnerReflejante,
            binding.spinnerKit,
            binding.spinnerAlarma,
            binding.spinnerTacos,
            binding.spinnerPortaEscalera,
            binding.spinnerPlacaLateral,
        )
        arrayBindingStateMovil.forEach { it.setSelection(0) }
        binding.txtAddiotionalEmployees.text.clear()
        binding.txtCheckCarDocPlaca.text.clear()
        binding.txtObservation.text.clear()
        binding.txtKM.text.clear()

        front = ""
        leftSide = ""
        rightSide = ""
        interior = ""
        rearLeftTire = ""
        rearRightTire = ""
        frontRightTire = ""
        frontLeftTire = ""

        binding.btnStatePhotoFront.text = "Subir Foto"
        binding.btnStatePhotoLeft.text = "Subir Foto"
        binding.btnStatePhotoRight.text = "Subir Foto"
        binding.btnStateInterior.text = "Subir Foto"
        binding.btnRearLeftTire.text = "Subir Foto"
        binding.btnRearRightTire.text = "Subir Foto"
        binding.btnFrontRightTire.text = "Subir Foto"
        binding.btnFrontLeftTire.text = "Subir Foto"
    }

    companion object {
        private const val TAG = "CameraXApp"
        private val request_permissions = arrayOf(
            Manifest.permission.CAMERA
        )
    }
}