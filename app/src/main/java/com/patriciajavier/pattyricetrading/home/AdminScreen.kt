package com.patriciajavier.pattyricetrading.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.patriciajavier.pattyricetrading.R
import com.patriciajavier.pattyricetrading.databinding.FragmentAdminScreenBinding
import com.patriciajavier.pattyricetrading.databinding.FragmentRegistrationScreenBinding
import com.patriciajavier.pattyricetrading.registration.arch.LoggedInViewModel
import com.patriciajavier.pattyricetrading.registration.arch.RegistrationLoginViewModel

class AdminScreen : Fragment() {

    private var _binding: FragmentAdminScreenBinding? = null
    private val binding get() = _binding!!

    private val viewModel : LoggedInViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.userMutableLiveData.observe(viewLifecycleOwner){
//            Log.d("Admin Screen", it.data.toString())
        }

        viewModel.isLoggedOutLiveData.observe(viewLifecycleOwner){ isLoggedOut ->
            if(isLoggedOut){
                findNavController().navigate(R.id.action_adminScreen_to_loginScreen)
            }
        }

        binding.button2.setOnClickListener {
            signOut()
        }

        //todo write logic here
        //todo sign out function
    }

    private fun signOut(){
        viewModel.logOut()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}