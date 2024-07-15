package com.hybrid.projectarea.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hybrid.projectarea.R
import com.hybrid.projectarea.databinding.FragmentImagesRectifiersBinding

class ImagesRectifiersFragment : Fragment() {
    private var _binding:FragmentImagesRectifiersBinding ? = null
    private val binding get() = _binding!!

    private var rectifiers_id:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rectifiers_id = requireArguments().getString("idRectifiers")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImagesRectifiersBinding.inflate(inflater,container,false)
        return binding.root
    }

}