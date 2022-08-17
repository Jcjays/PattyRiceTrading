package com.patriciajavier.pattyricetrading.home.admin.inventory.product

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.patriciajavier.pattyricetrading.databinding.FragmentRestockProductScreenBinding
import com.patriciajavier.pattyricetrading.firestore.models.Product
import com.patriciajavier.pattyricetrading.firestore.models.Response
import kotlinx.coroutines.flow.collect

class RestockProductScreen : Fragment() {

    private var _binding : FragmentRestockProductScreenBinding? = null
    private val binding get() = _binding!!

    private val args : RestockProductScreenArgs by navArgs()
    private val viewModel : ProductInfoScreenViewModel by activityViewModels()
    private var product : Product? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRestockProductScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        args.productId.let {
            viewModel.getProductFromFireStore(it)
        }

        viewModel.getProductLiveData.observe(viewLifecycleOwner){ response ->
            when(response){
                is Response.Loading -> binding.loadingState.root.isVisible = true
                is Response.Success -> {
                    product = response.data

                    binding.labelCurrentStockRestockScreen.text = "Current stock: ${response.data!!.stock}"

                    binding.updateStockEditText.doOnTextChanged { text, start, before, count ->
                        if(text!!.isNotBlank())
                            binding.labelTotalStockRestockScreen.text ="Total stock: ${response.data.stock + text.toString().toInt()}"
                        else
                            binding.labelTotalStockRestockScreen.text = "Total stock: ${response.data.stock}"
                    }

                    binding.labelCurrentPriceRestockScreen.text = "Current Price ${response.data.unitPrice}"

                    binding.updatePriceEditText.doOnTextChanged { text, start, before, count ->
                        if(text!!.isNotBlank())
                            binding.labelUpdatedPriceRestockScreen.text = "Updated price: $text"
                    }

                    binding.loadingState.root.isGone = true
                }
                is Response.Failure -> Toast.makeText(requireContext(), response.e.message.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.eventFLow.collect{ event ->
                when(event){
                    is Response.Loading -> binding.loadingState.root.isVisible = true
                    is Response.Failure -> Toast.makeText(requireContext(), event.e.message.toString(), Toast.LENGTH_SHORT).show()
                    is Response.Success -> {
                        Toast.makeText(requireContext(), event.data.toString(), Toast.LENGTH_SHORT).show()
                        binding.loadingState.root.isVisible = false
                    }
                }
            }
        }

        binding.saveUpdateProduct.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Notice")
                .setMessage("Please make sure all the information are correct before proceeding. You cannot UNDO this operation.")
                .setNegativeButton("Cancel") { dialog, which ->
                    dialog.dismiss()
                }
                .setPositiveButton("Proceed") { dialog, which ->
                    validateInput()
                }
                .show()
        }
    }

    private fun validateInput() {
        if(product == null){
            return
        }

        if(binding.updatePriceEditText.text!!.isEmpty() && binding.updateStockEditText.text!!.isEmpty()){
            Toast.makeText(requireContext(), "Nothing need to be change", Toast.LENGTH_SHORT).show()
            return
        }

        val newStock = if(binding.updateStockEditText.text!!.isEmpty()){
            product!!.stock
        }else{
            binding.updateStockEditText.text.toString().trim().toInt() + product!!.stock
        }

        val newPrice = if(binding.updatePriceEditText.text!!.isEmpty()){
            product!!.unitPrice
        }else{
            binding.updatePriceEditText.text.toString().trim().toDouble()
        }

        viewModel.updateProductFromFireStore(product!!.pId, newPrice, newStock)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}