package com.patriciajavier.pattyricetrading.home.admin.inventory

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.QuickContactBadge
import android.widget.Toast
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.patriciajavier.pattyricetrading.MyApp
import com.patriciajavier.pattyricetrading.R
import com.patriciajavier.pattyricetrading.databinding.FragmentInventoryScreenBinding
import com.patriciajavier.pattyricetrading.firestore.FirebaseService
import com.patriciajavier.pattyricetrading.firestore.models.Response
import com.patriciajavier.pattyricetrading.home.admin.inventory.order.OrderCardModel
import com.patriciajavier.pattyricetrading.home.admin.sales.SalesReportViewModel

class InventoryScreen : Fragment() {

    private var _binding : FragmentInventoryScreenBinding? = null
    private val binding get() = _binding!!

    private val viewModel : InventoryScreenViewModel by activityViewModels()
    private val epoxyController = InventoryScreenEpoxyController(::onItemClicked)

     fun onItemClicked(productId: String) {
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

        //observe inventory table in fire store
        //and add it on epoxy recycler view
        val accessRights = MyApp.accessRights

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
        //dagdag lang
        viewModel.updateViewStateLiveData.observe(viewLifecycleOwner) {
            if (it.exception != null) {
                Toast.makeText(requireContext(), it.exception.message, Toast.LENGTH_SHORT).show()
                return@observe
            }

            binding.loadingState.root.isVisible = it.isLoading

            if (it.data.isNotEmpty()) {
                epoxyController.response = it.data
            }
        }
        //end of dagdag
        binding.riceListEpoxyRecyclerView.setController(epoxyController)
        binding.addRiceInventoryScreen.setOnClickListener {
//            val action = InventoryScreenDirections.actionInventoryScreenToAddRiceScreen(args.uId)
            findNavController().navigate(R.id.action_inventoryScreen_to_addRiceScreen)
        }

        binding.OrdersInventoryScreen.setOnClickListener {
            val label = if(accessRights) "Orders" else "Restock"
            val action = InventoryScreenDirections.actionInventoryScreenToOrderModuleScreen(label)
            findNavController().navigate(action)
        }

        // sorting, dinagdagan
        val items = InventoryScreenViewModel.InventorySortedViewState.Sort.values()
        val adapter = ArrayAdapter(requireContext(), R.layout.sort_list_item, items)
        binding.autoCompleteTextView.setAdapter(adapter)

        //sort the data base on user wants.
        binding.autoCompleteTextView.doOnTextChanged { text, _, _, _ ->
            viewModel.currentSort = items.find { it.name == text.toString() }!!
        }

        binding.riceListEpoxyRecyclerView.setController(epoxyController)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}