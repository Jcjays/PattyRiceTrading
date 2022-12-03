package com.patriciajavier.pattyricetrading.home.admin.market.kilo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.patriciajavier.pattyricetrading.MyApp
import com.patriciajavier.pattyricetrading.R
import com.patriciajavier.pattyricetrading.databinding.FragmentAddRicePerKgBinding
import com.patriciajavier.pattyricetrading.firestore.models.Product

class AddRicePerKg : Fragment() {

    private var _binding : FragmentAddRicePerKgBinding? = null
    private val binding get() = _binding!!

    private val viewModel : ProductMarketKiloViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddRicePerKgBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val items = LinkedHashMap<String, Product>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.mutableListOfProducts.value?.forEach {
            items[it.productName] = it
        }

        val productName = ArrayList<String>()
        items.forEach {
            productName.add(it.key)
        }

        val adapter = ArrayAdapter(requireContext(), R.layout.sort_list_item, productName)
        binding.autoCompleteTextView.setAdapter(adapter)

        binding.saveProductKilo.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.confirm))
                .setMessage(getString(R.string.Add_this_item_on_your_kilo_sales))
                .setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                    dialog.dismiss()
                }
                .setPositiveButton(getString(R.string.confirm)) { dialog, which ->
                    validateInput()
                }
                .show()
        }
    }

    private fun validateInput() {
        val productName = binding.autoCompleteTextView.text.trim()

        if(productName.isEmpty()){
            binding.selectProductPerKg.error = "*Select a product"
            return
        }

        binding.selectProductPerKg.error = null

        val price = binding.productAddRicePerKgPrice.text!!.trim()
        if(price.isEmpty()){
            binding.productMarketPerKgUpdatePrice.error = "*Required"
            return
        }

        binding.productMarketPerKgUpdatePrice.error = null

        val product = items[productName.toString()] ?: return

        viewModel.saveProductPerKg(product, price.toString().toDouble(), MyApp.userId)
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}