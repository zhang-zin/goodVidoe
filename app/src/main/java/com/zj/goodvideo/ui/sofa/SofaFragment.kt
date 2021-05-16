package com.zj.goodvideo.ui.sofa

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zj.goodvideo.R
import com.zj.libnavannotation.FragmentDestination

@FragmentDestination(pageUrl = "main/tabs/sofa")
class SofaFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sofa, container, false)
    }

}