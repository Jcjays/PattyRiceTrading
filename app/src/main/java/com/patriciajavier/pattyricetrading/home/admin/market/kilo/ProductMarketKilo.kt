package com.patriciajavier.pattyricetrading.home.admin.market.kilo

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.patriciajavier.pattyricetrading.MyApp
import com.patriciajavier.pattyricetrading.R
import com.patriciajavier.pattyricetrading.databinding.FragmentProductMarketKiloBinding
import com.patriciajavier.pattyricetrading.firestore.models.ProductPerKg
import com.patriciajavier.pattyricetrading.firestore.models.Response
import java.util.concurrent.ConcurrentHashMap


class ProductMarketKilo : Fragment() {

    private var _binding : FragmentProductMarketKiloBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProductMarketKiloViewModel by activityViewModels()

    private val totalEpoxyController = TotalEpoxyControllerPerKg()

    private val epoxyController = ProductMarketKiloEpoxyController(::onItemClick, ::onRefillClick, ::onUpdateClick)

    private var updateInput = 0.0

    private fun onUpdateClick(pId: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        val input = EditText(requireContext())

        input.inputType = InputType.TYPE_CLASS_NUMBER
        builder.setView(input)

        builder.setTitle("Update price?")

        builder.setPositiveButton("Update") { dialog, which ->
            updateInput = input.text.toString().toDouble()
            viewModel.updateProductPerKgPrice(pId, MyApp.userId, updateInput)
            findNavController().navigate(R.id.action_productMarketKilo_self)
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.cancel() }

        builder.show()
    }

    private fun onItemClick(pId: String) {
        viewModel.addToCart(pId)
    }

    private fun onRefillClick(pId: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Refill this display?")
            .setMessage("Make sure that this display is empty to avoid over filling. This operation will open (1) sack on your inventory.")
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton("Confirm") { dialog, which ->
                viewModel.refillProductPerKg(pId, MyApp.userId)

                binding.loadingState.root.isVisible = true
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.loadingState.root.isGone = true
                    findNavController().navigate(R.id.action_productMarketKilo_self)
                }, 1500)
            }
            .show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProductMarketKiloBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getShopkeeperListOfProductsPerKg(MyApp.userId)
        viewModel.getShopkeeperListOfProducts(MyApp.userId)
        viewModel.clearCart()

        viewModel.getListOfProductsPerKg.observe(viewLifecycleOwner){ response ->
               when(response){
                   is Response.Loading -> binding.loadingState.root.isVisible = true
                   is Response.Failure -> Toast.makeText(requireContext(), "Error occurred", Toast.LENGTH_SHORT).show()
                   is Response.Success -> {
                       binding.loadingState.root.isGone = true
                       epoxyController.products = response.data
                   }
               }
        }

        val product = ConcurrentHashMap<String, ProductPerKg>()
        val list : ArrayList<ProductPerKg?> = ArrayList()
        val total : ArrayList<Double> = ArrayList()
        viewModel.cartContents.observe(viewLifecycleOwner){ result ->
            if(result != null){
                if(!product.containsKey(result.id))
                    product[result.id] = result

                val quantity = product.getValue(result.id).qty
                val currentProduct = product[result.id]!!

                if(currentProduct.qty < result.quantity)
                    currentProduct.qty = quantity + 1
                else{
                    Toast.makeText(requireContext(), "Not enough stock of ${result.productName}", Toast.LENGTH_SHORT).show()
                    return@observe
                }

                clearList(list, total, totalEpoxyController.product)

                product.forEach {
                    list.add(it.value)
                    total.add(it.value.qty * it.value.unitPrice)
                }

                var sum = 0.0
                total.forEach {
                    sum += it
                }

                binding.paymentOutlinedEditText.setText(sum.toString())
                totalEpoxyController.product = list
            }
        }


        binding.epoxyRecyclerViewProductMarketCashierList.setController(totalEpoxyController)
        binding.productMarketKiloEpoxyRecyclerView.setController(epoxyController)

        binding.clearProductsButton.setOnClickListener {
            clearList(list, total, totalEpoxyController.product)
            product.clear()
            binding.paymentOutlinedEditText.setText("")
        }

        binding.sellProductsButton.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Confirm Order")
                .setMessage("Make sure all details are correct.")
                .setNegativeButton("Cancel") { dialog, which ->
                    dialog.dismiss()
                }
                .setPositiveButton("Confirm") { dialog, which ->
                    viewModel.sellProductToCustomer(MyApp.userId, product)

                    clearList(list,total,totalEpoxyController.product)
                    product.clear()
                    binding.paymentOutlinedEditText.setText("")

                    binding.loadingState.root.isVisible = true
                    Handler(Looper.getMainLooper()).postDelayed({
                        binding.loadingState.root.isGone = true
                        findNavController().navigate(R.id.action_productMarketKilo_self)
                    }, 1500)

                }
                .show()
        }

        binding.addPerKilo.setOnClickListener {
            findNavController().navigate(R.id.action_productMarketKilo_to_addRicePerKg)
        }
    }


    private fun clearList(list: ArrayList<ProductPerKg?>, total: ArrayList<Double>, product: List<ProductPerKg?>){
        list.clear()
        total.clear()
        totalEpoxyController.product = emptyList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}