package com.patriciajavier.pattyricetrading.home.admin.inventory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.patriciajavier.pattyricetrading.MyApp
import com.patriciajavier.pattyricetrading.R
import com.patriciajavier.pattyricetrading.databinding.FragmentInventoryScreenBinding
import java.util.*

class InventoryScreen : Fragment() {

    private var _binding : FragmentInventoryScreenBinding? = null
    private val binding get() = _binding!!

    private val accessRights = MyApp.accessRights

    private val viewModel : InventoryScreenViewModel by activityViewModels()
    private val epoxyController = InventoryScreenEpoxyController(::onItemClicked)

     private fun onItemClicked(productId: String) {
        val action = InventoryScreenDirections.actionInventoryScreenToProductInfoScreen(productId)
        findNavController().navigate(action)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInventoryScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUserAccessRights()
        initObservables()
        initViews()

    }

    private fun initObservables() {
        viewModel.getListOfProducts.observe(viewLifecycleOwner){ response ->
            binding.loadingState.root.isGone = response.isLoadingDone
            epoxyController.response = response.product
        }

        binding.riceListEpoxyRecyclerView.setController(epoxyController)
    }

    private fun initUserAccessRights() {
        //fetch the appropriate data
        if(accessRights){
            viewModel.getAdminListOfProducts()
            binding.addRiceInventoryScreen.isVisible = true
            binding.OrdersInventoryScreen.text = "Orders"

        } else{
            viewModel.getShopkeeperListOfProducts(MyApp.userId)
            binding.addRiceInventoryScreen.isGone = true
            binding.OrdersInventoryScreen.text = "Restock"
        }
    }

    private fun initViews() {
        binding.addRiceInventoryScreen.setOnClickListener {
            findNavController().navigate(R.id.action_inventoryScreen_to_addRiceScreen)
        }

        binding.OrdersInventoryScreen.setOnClickListener {
            val label = if(accessRights) "Orders" else "Restock"
            val action = InventoryScreenDirections.actionInventoryScreenToOrderModuleScreen(label)
            findNavController().navigate(action)
        }

        val sortOptions = SortOptions.getSortNames()
        val adapter = ArrayAdapter(requireContext(), R.layout.sort_list_item, sortOptions)
            binding.autoCompleteTextView.setAdapter(adapter)

        binding.autoCompleteTextView.doOnTextChanged { text, _, _, _ ->
            viewModel.sortProducts(SortOptions.getSelection(text.toString()))

            Timer().schedule(object : TimerTask() {
                override fun run() {
                    binding.riceListEpoxyRecyclerView.smoothScrollToPosition(0)
                }
            }, 500)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}