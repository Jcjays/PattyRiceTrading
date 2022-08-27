package com.patriciajavier.pattyricetrading.home.admin.inventory.addproduct

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.patriciajavier.pattyricetrading.Constant
import com.patriciajavier.pattyricetrading.MyApp
import com.patriciajavier.pattyricetrading.databinding.FragmentAddRiceScreenBinding
import com.patriciajavier.pattyricetrading.firestore.models.Product
import com.patriciajavier.pattyricetrading.firestore.models.Response
import java.util.*


class AddRiceScreen : Fragment() {

    private var _binding : FragmentAddRiceScreenBinding? = null
    private val binding get() = _binding!!
    private val TAG = "AddRiceScreen"

    private val viewModel : AddRiceViewModel by activityViewModels()
    private var uriHolder : Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddRiceScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val galleryActivityLauncher: ActivityResultLauncher<Array<String>> =
            registerForActivityResult(
                ActivityResultContracts.OpenDocument()
            ) { result ->
                if (result != null) {
                    uriHolder = result
                    binding.riceImageAddRiceScreen.setImageURI(result)
                    binding.removeImageButtonAddRiceScreen.isVisible = true
                } else {
                    Log.d(TAG, "onActivityResult: the result is null for some reason")
                }
            }

        lifecycleScope.launchWhenStarted {
            viewModel.eventFLow.collect{ event ->
                when(event){
                    is Response.Loading -> binding.loadingState.root.isVisible = true
                    is Response.Success -> {
                        Toast.makeText(requireContext(), event.data.toString(), Toast.LENGTH_SHORT).show()
                        binding.loadingState.root.isGone = true
                        findNavController().navigateUp()
                    }
                    is Response.Failure -> Toast.makeText(requireContext(), event.e.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.removeImageButtonAddRiceScreen.setOnClickListener {
            binding.riceImageAddRiceScreen.setImageURI(null)
            binding.removeImageButtonAddRiceScreen.isVisible = false
        }

        binding.addProductImageButtonAddRiceScreen.setOnClickListener {
            galleryActivityLauncher.launch(arrayOf("image/*"))
        }

        binding.saveAddRiceScreen.setOnClickListener {
            validateInput()
        }
    }

    private fun validateInput() {
        val productTitle = binding.productNameEditTextAddRiceScreen.text.toString().trim().replaceFirstChar { it.uppercase() }

        if(productTitle.isEmpty()){
            binding.productNameTextFieldAddRiceScreen.error = Constant.TEXT_FIELD_ERROR_MSG
            return
        }
        //removing error
        binding.productNameTextFieldAddRiceScreen.error = null
        binding.productNameTextFieldAddRiceScreen.isErrorEnabled = false

        //-------------------------------------------------------------------

        val productDescription = binding.productDescEditTextAddRiceScreen.text.toString().trim().replaceFirstChar { it.uppercase() }

        //-------------------------------------------------------------------

        val stocks = binding.productStockEditTextAddRiceScreen.text.toString().trim()

        if(stocks.isEmpty()){
            binding.productStockTextFieldAddRiceScreen.error = Constant.TEXT_FIELD_ERROR_MSG
            return
        }

        //removing error
        binding.productStockTextFieldAddRiceScreen.error = null
        binding.productStockTextFieldAddRiceScreen.isErrorEnabled = false

        //-------------------------------------------------------------------

        val unitPrice = binding.productPriceEditTextAddRiceScreen.text.toString().trim()

        if(unitPrice.isEmpty()){
            binding.productPriceTextFieldAddRiceScreen.error = Constant.TEXT_FIELD_ERROR_MSG
            return
        }
        //removing error
        binding.productStockTextFieldAddRiceScreen.error = null
        binding.productStockTextFieldAddRiceScreen.isErrorEnabled = false
        //-------------------------------------------------------------------

        val checkRadioGroup = binding.radioGroup.checkedRadioButtonId
        val checkKilogramsPerSack = if(checkRadioGroup == binding.radioButton25kg.id) 25 else 50
        //-------------------------------------------------------------------

        if(uriHolder == null){
            Toast.makeText(requireContext(), "Please upload a product image", Toast.LENGTH_SHORT).show()
            return
        }

        val productInfo = Product(
            pId = UUID.randomUUID().toString(),
            productImage =  uriHolder.toString(),
            productName = productTitle,
            productDesc = productDescription,
            kiloPerSack = checkKilogramsPerSack,
            stock = stocks.toInt(),
            unitPrice = unitPrice.toDouble()
        )

        viewModel.addAdminProductToFireStore(productInfo)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}