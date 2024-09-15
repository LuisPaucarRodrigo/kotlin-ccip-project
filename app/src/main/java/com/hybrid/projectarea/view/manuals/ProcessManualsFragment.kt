package com.hybrid.projectarea.view.manuals

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.hybrid.projectarea.R
import com.hybrid.projectarea.api.ApiService
import com.hybrid.projectarea.api.AuthManager
import com.hybrid.projectarea.databinding.FragmentProcessManualsBinding
import com.hybrid.projectarea.model.FolderArchiveResponse
import com.hybrid.projectarea.model.FormProcessManuals
import com.hybrid.projectarea.model.GetProcessManuals
import com.hybrid.projectarea.model.RetrofitClient
import com.hybrid.projectarea.model.TokenAuth
import com.hybrid.projectarea.view.DeleteTokenAndCloseSession
import com.hybrid.projectarea.view.preproject.PreProjectFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.io.File
import java.io.IOException
import java.io.OutputStream

class ProcessManualsFragment : Fragment() {
    private var _binding: FragmentProcessManualsBinding? = null
    private val binding get() = _binding!!
    private var previousPath = ""
    private var currentPath = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProcessManualsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Manuales"

        requestManuals()

        binding.recyclerviewManuals.swipe.setColorSchemeResources(
            R.color.azulccip,
            R.color.greenccip
        )
        binding.recyclerviewManuals.swipe.setProgressBackgroundColorSchemeResource(R.color.white)
        binding.recyclerviewManuals.swipe.setOnRefreshListener {
            if (previousPath.isEmpty() && currentPath === "LocalDrive") {
                requestManuals()
            } else {
                requestManuals("", currentPath)
            }
        }

        binding.previousPath.setOnClickListener {
            if (previousPath.isEmpty() && currentPath != "LocalDrive") {
                binding.previousPath.isVisible = false
                requestManuals()
            } else {
                requestManuals("", previousPath)
            }
        }
    }

    private fun requestManuals(root: String = "1", path: String = "") {

        val arrayList = ArrayList<GetProcessManuals>()
        binding.recyclerviewManuals.recyclerview.layoutManager = LinearLayoutManager(context)
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val token = TokenAuth.getToken(requireContext(), "token")
                val apiService = RetrofitClient.getClient(token).create(ApiService::class.java)
                val authManager = AuthManager(apiService)

                authManager.funGetProcessManuals(
                    token,
                    FormProcessManuals(root, path),
                    object : AuthManager.inGetProcessManuals {
                        override fun onProcessManualsSuccess(response: FolderArchiveResponse) {
                            binding.shimmerManuals.beforeViewElement.isVisible = false
                            binding.recyclerviewManuals.afterViewElement.isVisible = true
                            binding.recyclerviewManuals.swipe.isRefreshing = false

                            previousPath = response.previousPath
                            currentPath = response.currentPath

                            if (previousPath != "" || response.currentPath != "LocalDrive") {
                                binding.previousPath.isVisible = true
                            }
                            response.folders_archives.forEach { item ->
                                val element =
                                    GetProcessManuals(item.name, item.type, item.path, item.size)
                                arrayList.add(element)
                            }
                            val adapter = AdapterProcessManuals(
                                arrayList,
                                object : AdapterProcessManuals.OnItemClickListener {
                                    override fun onItemClick(position: Int) {
                                        val item = arrayList[position]
                                        when (item.type) {
                                            "folder" -> {
                                                requestManuals("", item.path)
                                            }

                                            "archive" -> downloadArchive(item.path)
                                        }

                                    }
                                })
                            binding.recyclerviewManuals.recyclerview.adapter = adapter
                        }

                        override fun onProcessManualsNoAuthenticated() {
                            DeleteTokenAndCloseSession(this@ProcessManualsFragment)
                        }

                        override fun onProcessManualsFailed(errorMessage: String) {
                            binding.recyclerviewManuals.swipe.isRefreshing = false
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
                        }
                    })
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Se produjo un error inesperado. Por favor inténtalo de nuevo.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun downloadArchive(path: String) {
        Toast.makeText(requireContext(),"Descargando archivo. Un momento por favor.",Toast.LENGTH_LONG).show()
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val token = TokenAuth.getToken(requireContext(), "token")
                val apiService =
                    RetrofitClient.getClient(token).create(ApiService::class.java)
                val authManager = AuthManager(apiService)

                authManager.funGetDownloadManuals(
                    token,
                    path,
                    object : AuthManager.inGetDownloadManuls {
                        override fun onDownloadManualsSuccess(response: ResponseBody) {
                            val fileName = path.removePrefix("LocalDrive/")
                            lifecycleScope.launch(Dispatchers.IO) {
                                val fileUri =
                                    savePdfToDownloads(requireContext(), response, fileName)
                                fileUri?.let {
                                    withContext(Dispatchers.Main) {
                                        openPdf(requireContext(), fileUri)
                                    }
                                }
                            }
                        }

                        override fun onDownloadManualsNoAuthenticated() {
                            DeleteTokenAndCloseSession(this@ProcessManualsFragment)
                        }

                        override fun onDownloadManualsFailed(errorMessage: String) {
                            Toast.makeText(
                                requireContext(),
                                errorMessage,
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                    })
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Se produjo un error inesperado. Por favor inténtalo de nuevo.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    suspend fun savePdfToDownloads(
        context: Context,
        response: ResponseBody,
        fileName: String
    ): Uri? {
        return withContext(Dispatchers.IO) {
            try {
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }

                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                uri?.let {
                    var outputStream: OutputStream? = null
                    try {
                        outputStream = resolver.openOutputStream(uri)
                        outputStream?.use {
                            response.byteStream().copyTo(it)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } finally {
                        outputStream?.close()
                    }
                }

                return@withContext uri // Devuelve la URI directamente
            } catch (e: IOException) {
                e.printStackTrace()
                return@withContext null // Devuelve null en caso de error
            }
        }
    }


    private fun openPdf(context: Context, uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Open PDF"))
    }

}