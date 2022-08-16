package com.patriciajavier.pattyricetrading.home.admin.inventory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.patriciajavier.pattyricetrading.R
import com.patriciajavier.pattyricetrading.databinding.FragmentInventoryScreenBinding
import com.patriciajavier.pattyricetrading.firestore.models.Response
import com.patriciajavier.pattyricetrading.home.admin.account.profile.AccountProfileScreenDirections

class InventoryScreen : Fragment() {

    private var _binding : FragmentInventoryScreenBinding? = null
    private val binding get() = _binding!!

    private val viewModel : InventoryScreenViewModel by activityViewModels()
    private val epoxyController = InventoryScreenEpoxyController(::onItemClicked)

    private fun onItemClicked(productId: String) {
        val action = InventoryScreenDirections.actionInventoryScreenToProductInfoScreen(productId)
        findNavController().navigate(action)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInventoryScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //observe inventory table in fire store
        //and add it on epoxy recycler view
        viewModel.getListOfProducts()

        viewModel.getListOfProducts.observe(viewLifecycleOwner){ response ->
            when(response){
                is Response.Loading -> binding.loadingState.root.isVisible = true
                is Response.Success -> {
                    epoxyController.response = response.data
                    binding.loadingState.root.isGone = true
                }
                is Response.Failure -> Toast.makeText(requireContext(), response.e.message.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        binding.riceListEpoxyRecyclerView.setController(epoxyController)

        binding.addRiceInventoryScreen.setOnClickListener {
            findNavController().navigate(R.id.action_inventoryScreen_to_addRiceScreen)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}