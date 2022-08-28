package com.patriciajavier.pattyricetrading.home.admin.inventory.product

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
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.patriciajavier.pattyricetrading.MyApp
import com.patriciajavier.pattyricetrading.databinding.FragmentProductInfoScreenBinding
import com.patriciajavier.pattyricetrading.firestore.models.Product
import com.patriciajavier.pattyricetrading.firestore.models.Response


class ProductInfoScreen : Fragment() {

    private var _binding : FragmentProductInfoScreenBinding? = null
    private val binding get() = _binding!!

    private val args : ProductInfoScreenArgs by navArgs()
    private val viewModel : ProductInfoScreenViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProductInfoScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        args.let {
            if(MyApp.accessRights)
                viewModel.getAdminProductFromFireStore(it.productId)
            else
                viewModel.getShopkeeperProductFromFireStore(it.productId, MyApp.userId)
        }

        var product : Product? = null


        viewModel.getProductLiveData.observe(viewLifecycleOwner){ response ->
            when(response){
                is Response.Loading -> binding.loadingState.root.isVisible = true
                is Response.Success -> {

                    product = response.data

                    Glide.with(requireContext())
                        .load(response.data!!.productImage)
                        .into(binding.productImageProductInfoScreen)

                    binding.productNameProductInfoScreen.text = response.data.productName
                    binding.productDescProductInfoScreen.text = response.data.productDesc
                    binding.productStockProductInfoScreen.text = "Stocks Left: \n${response.data.stock}"
                    binding.productKilosPerSackProductInfoScreen.text = "Kg per sack: \n${response.data.kiloPerSack}kg"
                    binding.productPriceProductInfoScreen.text = "Unit Price: \n${response.data.unitPrice}"

                    binding.loadingState.root.isGone = true
                }
                is Response.Failure -> Toast.makeText(requireContext(), response.e.message.toString(), Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.deleteProductLiveData.observe(viewLifecycleOwner){ response ->
            when(response){
                is Response.Loading -> binding.loadingState.root.isVisible = true
                is Response.Success -> {
                    Toast.makeText(requireContext(), response.data, Toast.LENGTH_SHORT).show()
                    binding.loadingState.root.isGone = true
                    viewModel.clearDeleteLiveData()
                    findNavController().navigateUp()
                }
                is Response.Failure -> Toast.makeText(requireContext(), response.e.message.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        binding.restockProductInfoScreen.setOnClickListener {
            if(product != null){
                val action = ProductInfoScreenDirections.actionProductInfoScreenToRestockProductScreen(product!!.pId)
                findNavController().navigate(action)
                return@setOnClickListener
            }
            Toast.makeText(requireContext(), "unknown error occurred.", Toast.LENGTH_SHORT).show()
        }

        binding.deleteProductInfoScreen.setOnClickListener {
            if(product != null){

                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Notice")
                    .setMessage("Please make sure all the information are correct before proceeding. You cannot UNDO this operation.")
                    .setNegativeButton("Cancel") { dialog, which ->
                        dialog.dismiss()
                    }
                    .setPositiveButton("Proceed") { dialog, which ->
                        if(MyApp.accessRights)
                            viewModel.deleteAdminProduct(product!!.pId)
                        else
                            viewModel.deleteShopkeeperProduct(product!!.pId, MyApp.userId)
                    }
                    .show()

                return@setOnClickListener
            }
            Toast.makeText(requireContext(), "unknown error occurred.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}