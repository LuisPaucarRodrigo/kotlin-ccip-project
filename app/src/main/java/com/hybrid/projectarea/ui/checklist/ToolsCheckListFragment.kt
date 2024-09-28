package com.hybrid.projectarea.ui.checklist

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.DisplayMetrics
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.OrientationEventListener
import android.view.ScaleGestureDetector
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.hybrid.projectarea.R
import com.hybrid.projectarea.api.ApiService
import com.hybrid.projectarea.api.AuthManager

import com.hybrid.projectarea.databinding.FragmentToolsCheckListBinding
import com.hybrid.projectarea.domain.model.checkListTools
import com.hybrid.projectarea.utils.Alert
import com.hybrid.projectarea.utils.HideKeyboard
import com.hybrid.projectarea.model.RequestPermissions

import com.hybrid.projectarea.model.RetrofitClient
import com.hybrid.projectarea.model.TokenAuth
import com.hybrid.projectarea.utils.encodeImage
import com.hybrid.projectarea.utils.rotateAndCreateBitmap
import com.hybrid.projectarea.ui.DeleteTokenAndCloseSession
import com.hybrid.projectarea.utils.aspectRatio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


class ToolsCheckListFragment : Fragment() {
    private var _binding: FragmentToolsCheckListBinding? = null
    private val binding get() = _binding!!

    private var goodTools: String = ""
    private var badTools: String = ""

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var file: File
    private var identifierBtn: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentToolsCheckListBinding.inflate(inflater, container, false)
        binding.scrollChecklist.checkListTools.apply {
            setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.azulccip))
            setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }
        val optionsTools = listOf("Bueno", "Regular", "Malo", "Solicitud", "No aplica")
        val optionsReasons = listOf("Revision Periódica", "Cambio de Personal")
        val optionsZone =
            listOf("Arequipa", "Chala", "Moquegua Pint", "Moquegua Pext", "Tacna", "MDD1", "MDD2")

        val adapterReason =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, optionsReasons)
        binding.spinnerReason.adapter = adapterReason
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, optionsZone)

        binding.spinnerZone.adapter = adapter
        val spinnerToolBindings = listOf(
            binding.spinnerMosqueton,
            binding.spinnerPelacable,
            binding.spinnerCrimpeadora,
            binding.spinnerCrimpeadoraTerminales,
            binding.spinnerLimas,
            binding.spinnerAllen,
            binding.spinnerKitReadline,
            binding.spinnerImpacto,
            binding.spinnerDielectricos,
            binding.spinnerCorte,
            binding.spinnerFuerza,
            binding.spinnerRecto,
            binding.spinnerFrancesas,
            binding.spinnerSierra,
            binding.spinnerSilicona,
            binding.spinnerPolea,
            binding.spinnerWincha,
            binding.spinnerEslinga,
            binding.spinnerBotiquin,
            binding.spinnerBrocas,
            binding.spinnerSacabocado,
            binding.spinnerExtractor,
            binding.spinnerJuegoLlaves,
            binding.spinnerJuegoDadosBravos,
            binding.spinnerCuter,
            binding.spinnerThor,
            binding.spinnerMaletaGrande,
            binding.spinnerMaletaMediana,

            binding.spinnerCarroAnticaidas,
            binding.spinnerArnes,

            binding.spinnerHidrolavadora,
            binding.spinnerSopladora,
            binding.spinnerMegometro,
            binding.spinnerTelurometro,
            binding.spinnerAperimetrica,
            binding.spinnerManometro,
            binding.spinnerPirometro,
            binding.spinnerLaptop,
            binding.spinnerTaladro,
            binding.spinnerBrujula,
            binding.spinnerInclilometro,
            binding.spinnerLinterna,
            binding.spinnerPowerMeter,
            binding.spinnerPistola,
            binding.spinnerExtension,
            binding.spinnerPistolaEstano,
            binding.spinnerEscaleraTijera,
            binding.spinnerPulverizadora,
            binding.spinnerRJ45,
            binding.spinnerConsolorRed,
            binding.spinnerAdaptadorRed,

            binding.spinnerPertiga,
            binding.spinnerSoga75,
            binding.spinnerCuter,
            binding.spinnerEscalera,
            binding.spinnerLongCable,
            binding.spinnerPadlock,
            binding.spinnerChains,
            binding.spinnerHose,
            binding.spinnerCorporatePhone,
        )

        spinnerToolBindings.forEach { spinner ->
            spinner.adapter = ArrayAdapter(
                requireContext(), android.R.layout.simple_list_item_1, optionsTools
            )
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()
        binding.scrollChecklist.checkListDay.setOnClickListener {
            openFragment(R.id.action_ToolsCheckListFragment_to_DayCheckListFragment)
        }

        binding.scrollChecklist.checkListEpps.setOnClickListener {
            openFragment(R.id.action_ToolsCheckListFragment_to_EppsCheckListFragment)
        }

        binding.scrollChecklist.checkListMobile.setOnClickListener {
            openFragment(R.id.action_ToolsCheckListFragment_to_MobileCheckListFragment)
        }

        binding.scrollChecklist.checkListHistory.setOnClickListener {
            openFragment(R.id.action_ToolsCheckListFragment_to_HistoryCheckListFragment)
        }

        binding.send.buttonSend.setOnClickListener {
            val formTools = collectFormData()
            if (areAllFieldsFilled(formTools)) {
                HideKeyboard.hideKeyboard(binding.root)
                binding.send.buttonSend.isEnabled = false
                sendCheckListTools(formTools)
            } else {
                Toast.makeText(context, "Por favor, llena todos los campos", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.btnBadTools.setOnClickListener {
            selectionCameraAndPhoto()
            identifierBtn = "badTools"
        }

        binding.btnGoodTools.setOnClickListener {
            selectionCameraAndPhoto()
            identifierBtn = "goodTools"
        }

        binding.captureButton.setOnClickListener {
            takePhoto { photo ->
                when (identifierBtn) {
                    "badTools" -> {
                        badTools = photo
                        binding.btnBadTools.text = "Imagen Subida"
                    }

                    "goodTools" -> {
                        goodTools = photo
                        binding.btnGoodTools.text = "Imagen Subida"
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
    }

    private fun sendCheckListTools(formTools: checkListTools) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val token = TokenAuth.getToken(requireContext(), "token")
                val apiService = RetrofitClient.getClient(token)
                val authManager = AuthManager(apiService)
                authManager.funPostCheckListTools(
                    token,
                    formTools,
                    object : AuthManager.incheckListTools {
                        override fun onStoreCheckListToolsSuccess() {
                            lifecycleScope.launch(Dispatchers.Main) {
                                Alert.alertSuccess(requireContext(), layoutInflater)
                                dataCleaning()
                                binding.send.buttonSend.isEnabled = true
                            }
                        }

                        override fun onStoreCheckListToolsNoAuthenticated() {
                            DeleteTokenAndCloseSession(this@ToolsCheckListFragment)
                        }

                        override fun onStoreCheckListToolsFailed(errorMessage: String) {
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

    private fun selectionCameraAndPhoto() {
        HideKeyboard.hideKeyboard(binding.root)
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

        val screenAspectRatio = aspectRatio()

        cameraProviderFuture.addListener({
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

    private fun openFragment(id: Int) {
        findNavController().navigate(id)
    }

    private fun collectFormData(): checkListTools {
        return checkListTools(
            reason = binding.spinnerReason.selectedItem.toString(),
            additionalEmployees = binding.txtAddiotionalEmployees.text.toString(),
            zone = binding.spinnerZone.selectedItem.toString(),
            carabiner = binding.spinnerMosqueton.selectedItem.toString(),
            wireStripper = binding.spinnerPelacable.selectedItem.toString(),
            crimper = binding.spinnerCrimpeadora.selectedItem.toString(),
            terminalCrimper = binding.spinnerCrimpeadoraTerminales.selectedItem.toString(),
            files = binding.spinnerLimas.selectedItem.toString(),
            allenKeys = binding.spinnerAllen.selectedItem.toString(),
            readlineKit = binding.spinnerKitReadline.selectedItem.toString(),
            impactWrench = binding.spinnerImpacto.selectedItem.toString(),
            dielectricTools = binding.spinnerDielectricos.selectedItem.toString(),
            cuttingTools = binding.spinnerCorte.selectedItem.toString(),
            forceps = binding.spinnerFuerza.selectedItem.toString(),
            straightWrench = binding.spinnerRecto.selectedItem.toString(),
            frenchWrench = binding.spinnerFrancesas.selectedItem.toString(),
            saw = binding.spinnerSierra.selectedItem.toString(),
            silicone = binding.spinnerSilicona.selectedItem.toString(),
            pulley = binding.spinnerPolea.selectedItem.toString(),
            tapeMeasure = binding.spinnerWincha.selectedItem.toString(),
            sling = binding.spinnerEslinga.selectedItem.toString(),
            kit = binding.spinnerBotiquin.selectedItem.toString(),
            drillBits = binding.spinnerBrocas.selectedItem.toString(),
            punch = binding.spinnerSacabocado.selectedItem.toString(),
            extractor = binding.spinnerExtractor.selectedItem.toString(),
            wrenchSet = binding.spinnerJuegoLlaves.selectedItem.toString(),
            braveDices = binding.spinnerJuegoDadosBravos.selectedItem.toString(),
            cutter = binding.spinnerCuter.selectedItem.toString(),
            hammer = binding.spinnerThor.selectedItem.toString(),
            largeToolBag = binding.spinnerMaletaGrande.selectedItem.toString(),
            mediumToolBag = binding.spinnerMaletaMediana.selectedItem.toString(),
            fallProtectionCar = binding.spinnerCarroAnticaidas.selectedItem.toString(),
            harness = binding.spinnerArnes.selectedItem.toString(),
            pressureWasher = binding.spinnerHidrolavadora.selectedItem.toString(),
            blower = binding.spinnerSopladora.selectedItem.toString(),
            megommeter = binding.spinnerMegometro.selectedItem.toString(),
            earthTester = binding.spinnerTelurometro.selectedItem.toString(),
            perimeterMeter = binding.spinnerAperimetrica.selectedItem.toString(),
            manometer = binding.spinnerManometro.selectedItem.toString(),
            pyrometer = binding.spinnerPirometro.selectedItem.toString(),
            laptop = binding.spinnerLaptop.selectedItem.toString(),
            drill = binding.spinnerTaladro.selectedItem.toString(),
            compass = binding.spinnerBrujula.selectedItem.toString(),
            inclinometer = binding.spinnerInclilometro.selectedItem.toString(),
            flashlight = binding.spinnerLinterna.selectedItem.toString(),
            powerMeter = binding.spinnerPowerMeter.selectedItem.toString(),
            glueGun = binding.spinnerPistola.selectedItem.toString(),
            solderingGun = binding.spinnerPistolaEstano.selectedItem.toString(),
            stepLadder = binding.spinnerEscaleraTijera.selectedItem.toString(),
            sprayer = binding.spinnerPulverizadora.selectedItem.toString(),
            rj45Connector = binding.spinnerRJ45.selectedItem.toString(),
            networkConsole = binding.spinnerConsolorRed.selectedItem.toString(),
            networkAdapter = binding.spinnerAdaptadorRed.selectedItem.toString(),
            hotStick = binding.spinnerPertiga.selectedItem.toString(),
            rope75 = binding.spinnerSoga75.selectedItem.toString(),
            ladder = binding.spinnerEscalera.selectedItem.toString(),
            extensionCord = binding.spinnerExtension.selectedItem.toString(),
            longCable = binding.spinnerLongCable.selectedItem.toString(),
            padlock = binding.spinnerPadlock.selectedItem.toString(),
            chains = binding.spinnerChains.selectedItem.toString(),
            hose = binding.spinnerHose.selectedItem.toString(),
            corporatePhone = binding.spinnerCorporatePhone.selectedItem.toString(),
            observation = binding.txtObservation.text.toString(),
            badTools = badTools,
            goodTools = goodTools
        )
    }

    private fun areAllFieldsFilled(formData: checkListTools): Boolean {
        return formData.reason.isNotEmpty() && formData.zone.isNotEmpty() &&
                formData.carabiner.isNotEmpty() && formData.wireStripper.isNotEmpty() &&
                formData.crimper.isNotEmpty() && formData.terminalCrimper.isNotEmpty() &&
                formData.files.isNotEmpty() && formData.allenKeys.isNotEmpty() &&
                formData.readlineKit.isNotEmpty() && formData.impactWrench.isNotEmpty() &&
                formData.dielectricTools.isNotEmpty() && formData.cuttingTools.isNotEmpty() &&
                formData.forceps.isNotEmpty() && formData.straightWrench.isNotEmpty() &&
                formData.frenchWrench.isNotEmpty() && formData.saw.isNotEmpty() &&
                formData.silicone.isNotEmpty() && formData.pulley.isNotEmpty() &&
                formData.tapeMeasure.isNotEmpty() && formData.sling.isNotEmpty() && formData.kit.isNotEmpty() &&
                formData.drillBits.isNotEmpty() && formData.punch.isNotEmpty() &&
                formData.extractor.isNotEmpty() && formData.wrenchSet.isNotEmpty() && formData.braveDices.isNotEmpty() &&
                formData.cutter.isNotEmpty() && formData.hammer.isNotEmpty() &&
                formData.largeToolBag.isNotEmpty() && formData.mediumToolBag.isNotEmpty() &&
                formData.fallProtectionCar.isNotEmpty() && formData.harness.isNotEmpty() &&
                formData.pressureWasher.isNotEmpty() && formData.blower.isNotEmpty() &&
                formData.megommeter.isNotEmpty() && formData.earthTester.isNotEmpty() &&
                formData.perimeterMeter.isNotEmpty() && formData.manometer.isNotEmpty() &&
                formData.pyrometer.isNotEmpty() && formData.laptop.isNotEmpty() &&
                formData.drill.isNotEmpty() && formData.compass.isNotEmpty() &&
                formData.inclinometer.isNotEmpty() && formData.flashlight.isNotEmpty() &&
                formData.powerMeter.isNotEmpty() && formData.glueGun.isNotEmpty() &&
                formData.solderingGun.isNotEmpty() &&
                formData.stepLadder.isNotEmpty() && formData.sprayer.isNotEmpty() &&
                formData.rj45Connector.isNotEmpty() && formData.networkConsole.isNotEmpty() &&
                formData.networkAdapter.isNotEmpty() && formData.hotStick.isNotEmpty() && formData.rope75.isNotEmpty() && formData.ladder.isNotEmpty() &&
                formData.extensionCord.isNotEmpty() &&
                formData.longCable.isNotEmpty() && formData.padlock.isNotEmpty() && formData.chains.isNotEmpty() &&
                formData.hose.isNotEmpty() && formData.corporatePhone.isNotEmpty()
    }

    private fun dataCleaning() {
        binding.spinnerReason.setSelection(0)
        binding.txtAddiotionalEmployees.text.clear()
        binding.spinnerZone.setSelection(0)

        binding.spinnerMosqueton.setSelection(0)
        binding.spinnerPelacable.setSelection(0)
        binding.spinnerCrimpeadora.setSelection(0)
        binding.spinnerCrimpeadoraTerminales.setSelection(0)
        binding.spinnerLimas.setSelection(0)
        binding.spinnerAllen.setSelection(0)
        binding.spinnerKitReadline.setSelection(0)
        binding.spinnerImpacto.setSelection(0)
        binding.spinnerDielectricos.setSelection(0)
        binding.spinnerCorte.setSelection(0)
        binding.spinnerFuerza.setSelection(0)
        binding.spinnerRecto.setSelection(0)
        binding.spinnerFrancesas.setSelection(0)
        binding.spinnerSierra.setSelection(0)
        binding.spinnerSilicona.setSelection(0)
        binding.spinnerPolea.setSelection(0)
        binding.spinnerWincha.setSelection(0)
        binding.spinnerEslinga.setSelection(0)
        binding.spinnerBotiquin.setSelection(0)
        binding.spinnerBrocas.setSelection(0)
        binding.spinnerSacabocado.setSelection(0)
        binding.spinnerExtractor.setSelection(0)
        binding.spinnerJuegoLlaves.setSelection(0)
        binding.spinnerJuegoDadosBravos.setSelection(0)
        binding.spinnerCuter.setSelection(0)
        binding.spinnerThor.setSelection(0)
        binding.spinnerMaletaGrande.setSelection(0)
        binding.spinnerMaletaMediana.setSelection(0)
        binding.spinnerCarroAnticaidas.setSelection(0)
        binding.spinnerArnes.setSelection(0)
        binding.spinnerHidrolavadora.setSelection(0)
        binding.spinnerSopladora.setSelection(0)
        binding.spinnerMegometro.setSelection(0)
        binding.spinnerTelurometro.setSelection(0)
        binding.spinnerAperimetrica.setSelection(0)
        binding.spinnerManometro.setSelection(0)
        binding.spinnerPirometro.setSelection(0)
        binding.spinnerLaptop.setSelection(0)
        binding.spinnerTaladro.setSelection(0)
        binding.spinnerBrujula.setSelection(0)
        binding.spinnerInclilometro.setSelection(0)
        binding.spinnerLinterna.setSelection(0)
        binding.spinnerPowerMeter.setSelection(0)
        binding.spinnerPistola.setSelection(0)
        binding.spinnerPistolaEstano.setSelection(0)
        binding.spinnerEscaleraTijera.setSelection(0)
        binding.spinnerPulverizadora.setSelection(0)
        binding.spinnerRJ45.setSelection(0)
        binding.spinnerConsolorRed.setSelection(0)
        binding.spinnerAdaptadorRed.setSelection(0)
        binding.spinnerPertiga.setSelection(0)
        binding.spinnerSoga75.setSelection(0)
        binding.spinnerEscalera.setSelection(0)
        binding.spinnerExtension.setSelection(0)

        binding.spinnerLongCable.setSelection(0)
        binding.spinnerPadlock.setSelection(0)
        binding.spinnerChains.setSelection(0)
        binding.spinnerHose.setSelection(0)
        binding.spinnerCorporatePhone.setSelection(0)

        badTools = ""
        goodTools = ""

        binding.txtObservation.text.clear()
        binding.btnBadTools.text = "Subir Foto"
        binding.btnGoodTools.text = "Subir Foto"
    }

    companion object {
        private const val TAG = "CameraXApp"
        private val request_permissions = arrayOf(
            Manifest.permission.CAMERA
        )
    }
}