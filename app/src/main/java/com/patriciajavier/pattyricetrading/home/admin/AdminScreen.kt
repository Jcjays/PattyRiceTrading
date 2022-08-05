package com.patriciajavier.pattyricetrading.home.admin

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
import com.patriciajavier.pattyricetrading.Constant
import com.patriciajavier.pattyricetrading.R
import com.patriciajavier.pattyricetrading.databinding.FragmentAdminScreenBinding
import com.patriciajavier.pattyricetrading.databinding.FragmentRegistrationScreenBinding
import com.patriciajavier.pattyricetrading.registration.arch.LoggedInViewModel
import com.patriciajavier.pattyricetrading.registration.arch.RegistrationLoginViewModel

class AdminScreen : Fragment() {

    private val TAG = "AdminScreen:"

    private var _binding: FragmentAdminScreenBinding? = null
    private val binding get() = _binding!!

    private val viewModel : LoggedInViewModel by activityViewModels()
    private val epoxyController = AdminDashboardEpoxyController(::onCardButtonClickListener)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //build dashboard
        binding.epoxyRecyclerView.setControllerAndBuildModels(epoxyController)

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
    }

    private fun signOut(){
        viewModel.logOut()
    }

    private fun onCardButtonClickListener(title: String){
       //todo navigate to specific views
        when(title){
            Constant.CARD_TITLE_ACCOUNT -> {
                findNavController().navigate(R.id.action_adminScreen_to_account)
            }
            Constant.CARD_TITLE_SALES_REPORT -> {
                findNavController().navigate(R.id.action_adminScreen_to_salesReportScreen)
            }
            Constant.CARD_TITLE_INVENTORY ->{
                findNavController().navigate(R.id.action_adminScreen_to_inventoryScreen)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}