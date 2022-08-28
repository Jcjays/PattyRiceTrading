package com.patriciajavier.pattyricetrading.home.admin.inventory.order

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.patriciajavier.pattyricetrading.MyApp
import com.patriciajavier.pattyricetrading.R
import com.patriciajavier.pattyricetrading.databinding.FragmentOrderModuleScreenBinding
import com.patriciajavier.pattyricetrading.firestore.models.Response
import java.util.*


class OrderModuleScreen : Fragment() {

    private var _binding : FragmentOrderModuleScreenBinding? = null
    private val binding get() = _binding!!

    private val viewModel : OrderModuleViewModel by activityViewModels()
    private val epoxyController = OrderModuleEpoxyController(::onItemClick, ::onOrderClick, ::onCancelOrder)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderModuleScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(MyApp.accessRights){
            viewModel.getPendingOrdersOfProducts()
        }else{
            viewModel.getAvailableListOfProducts()
        }

        //assign the access rights
        epoxyController.accessRights = MyApp.accessRights

        viewModel.getListOfProducts.observe(viewLifecycleOwner){ response ->
            when(response){
               is Response.Loading -> binding.loadingState.root.isVisible = true
               is Response.Failure -> Toast.makeText(requireContext(), response.e.message.toString(), Toast.LENGTH_SHORT).show()
                is Response.Success -> {
                    epoxyController.response = response.data
                    binding.loadingState.root.isGone = true
                }
            }
        }

        viewModel.getListOfOrders.observe(viewLifecycleOwner){ orders ->
            when(orders){
                is Response.Loading -> binding.loadingState.root.isVisible = true
                is Response.Failure -> Toast.makeText(requireContext(), orders.e.message.toString(), Toast.LENGTH_SHORT).show()
                is Response.Success -> {
                    epoxyController.orders = orders.data
                    binding.loadingState.root.isGone = true
                }
            }
        }

        binding.orderModuleRecyclerView.setController(epoxyController)
    }

    private fun onCancelOrder(oId: String, pId: String, uId: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Decline this order?")
            .setMessage("Are you sure? you cannnot undo this operation.")
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton("Proceed") { dialog, which ->
                viewModel.declineOrderAndLog(oId, pId, uId)

                binding.loadingState.root.isVisible = true
                Handler(Looper.myLooper()!!).postDelayed({
                    findNavController().navigate(R.id.action_orderModuleScreen_self)
                    binding.loadingState.root.isVisible = false
                    }, 2500 //Specific time in milliseconds
                )
            }
            .show()
    }

    private fun onOrderClick(oId: String, pId: String, uId: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Accept this order?")
            .setMessage("Please review all the information you cannot UNDO this operation.")
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton("Proceed") { dialog, which ->
                viewModel.proceedOrderAndLog(oId, pId, uId)

                binding.loadingState.root.isVisible = true
                Handler(Looper.myLooper()!!).postDelayed({
                    findNavController().navigate(R.id.action_orderModuleScreen_self)
                    binding.loadingState.root.isVisible = false
                }, 3000 //Specific time in milliseconds
                )
            }
            .show()
    }


    private fun onItemClick(pId: String) {
        val action = OrderModuleScreenDirections.actionOrderModuleScreenToOrderRequestScreen(pId)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}