package com.youngminds.upmarkx.fragments


import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.youngminds.upmarkx.R
import com.youngminds.upmarkx.databinding.FragmentBlankBinding


class BlankFragment : Fragment(R.layout.fragment_blank) {




    private lateinit var binding: FragmentBlankBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding = FragmentBlankBinding.bind(view)


        helle("hello")

    }



    private fun helle(text:String) {


        print(text)



    }
}