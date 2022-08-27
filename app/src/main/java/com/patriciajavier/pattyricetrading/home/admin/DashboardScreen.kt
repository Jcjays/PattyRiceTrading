package com.patriciajavier.pattyricetrading.home.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.patriciajavier.pattyricetrading.Constant
import com.patriciajavier.pattyricetrading.MyApp
import com.patriciajavier.pattyricetrading.R
import com.patriciajavier.pattyricetrading.databinding.FragmentDashboardScreenBinding
import com.patriciajavier.pattyricetrading.registration.arch.LoggedInViewModel
import com.patriciajavier.pattyricetrading.registration.arch.SharedViewModel

class DashboardScreen : Fragment() {

    private val TAG = "DashboardScreen:"

    private var _binding: FragmentDashboardScreenBinding? = null
    private val binding get() = _binding!!

    private val viewModel : LoggedInViewModel by activityViewModels()
    private val epoxyController = AdminDashboardEpoxyController(::onCardButtonClickListener)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //determine access rights
        epoxyController.userAccess = MyApp.accessRights

        //build dashboard
        binding.epoxyRecyclerView.setController(epoxyController)

        viewModel.getProfileData(MyApp.userId)

        viewModel.userMutableLiveData.observe(viewLifecycleOwner){ userInfo ->
            if(userInfo != null){
                epoxyController.userPriviledge = userInfo.isActive
                binding.textView2.text = if(userInfo.isActive) "Dashboard" else "Account Disabled"
            }
        }

        viewModel.isLoggedOutLiveData.observe(viewLifecycleOwner){ isLoggedOut ->
            if(isLoggedOut){
                findNavController().navigate(R.id.action_adminScreen_to_loginScreen)
            }
        }

        binding.button2.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Log out")
                .setMessage("Do you want to sign out your account?")
                .setNegativeButton("Cancel") { dialog, which ->
                    dialog.dismiss()
                }
                .setPositiveButton("Confirm") { dialog, which ->
                    signOut()
                }
                .show()

        }
    }

    private fun signOut(){
        viewModel.logOut()
    }

    private fun onCardButtonClickListener(title: String){
        when(title){
            Constant.CARD_TITLE_ACCOUNT -> findNavController().navigate(R.id.action_adminScreen_to_account)
            Constant.CARD_TITLE_SALES_REPORT -> findNavController().navigate(R.id.action_adminScreen_to_salesReportScreen)
            Constant.CARD_TITLE_INVENTORY -> findNavController().navigate(R.id.action_adminScreen_to_inventoryScreen)
            Constant.CARD_TITLE_TRANSACTION -> findNavController().navigate(R.id.action_adminScreen_to_salesReportScreen)
            Constant.CARD_TITLE_SALES -> findNavController().navigate(R.id.action_dashboardScreen_to_productMarketScreen)
            Constant.CARD_TITLE_SALES_KILO -> findNavController().navigate(R.id.action_dashboardScreen_to_productMarketKilo)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}