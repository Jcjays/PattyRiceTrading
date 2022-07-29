package com.patriciajavier.pattyricetrading

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.patriciajavier.pattyricetrading.databinding.FragmentAdminScreenBinding
import com.patriciajavier.pattyricetrading.databinding.FragmentShopkeeperScreenBinding

class ShopkeeperScreen : Fragment() {

    private var _binding: FragmentShopkeeperScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShopkeeperScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //todo write logic here
        //todo sign out function
    }
}