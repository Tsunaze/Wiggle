package com.tsunaze.wiggleexample.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tsunaze.wiggleexample.R

class TestBFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.testb_fragment, container, false)

    companion object {
        fun newInstance() = TestBFragment()
    }
}