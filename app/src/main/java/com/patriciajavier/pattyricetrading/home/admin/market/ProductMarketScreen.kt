package com.patriciajavier.pattyricetrading.home.admin.market

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.patriciajavier.pattyricetrading.MyApp
import com.patriciajavier.pattyricetrading.R
import com.patriciajavier.pattyricetrading.databinding.FragmentProductMarketScreenBinding
import com.patriciajavier.pattyricetrading.firestore.models.Product
import com.patriciajavier.pattyricetrading.firestore.models.Response
import java.util.concurrent.ConcurrentHashMap


class ProductMarketScreen : Fragment() {

    private var _binding : FragmentProductMarketScreenBinding? = null
    private val binding get() = _binding!!

    private val viewModel : ProductMarketViewModel by activityViewModels()
    private val epoxyController = ProductMarketEpoxyController(::onItemClicked)
    private val totalEpoxyController = TotalEpoxyController()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductMarketScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.clearCart()
        viewModel.getShopkeeperListOfProducts(MyApp.userId)


        viewModel.getListOfProducts.observe(viewLifecycleOwner){ response ->
            when(response){
                is Response.Loading -> binding.loadingState.root.isVisible = true
                is Response.Failure -> {
                    Toast.makeText(requireContext(), response.e.message, Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                is Response.Success ->{
                    epoxyController.products = response.data
                    binding.loadingState.root.isGone = true
                }
            }
        }

        val product = ConcurrentHashMap<String, Product >()
        val list : ArrayList<Product?> = ArrayList()
        val total : ArrayList<Double> = ArrayList()

        viewModel.cartContents.observe(viewLifecycleOwner){ result ->
            if(result != null){
                if(!product.containsKey(result.pId))
                    product[result.pId] = result

                val quantity = product.getValue(result.pId).qty
                val currentProduct = product[result.pId]!!

                if(currentProduct.qty < result.stock)
                    currentProduct.qty = quantity + 1
                else
                    Toast.makeText(requireContext(), "Not enough stock of ${result.productName}", Toast.LENGTH_SHORT).show()

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

        binding.clearProductsButton.setOnClickListener {
            clearList(list,total,totalEpoxyController.product)
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
                        findNavController().navigate(R.id.action_productMarketScreen_self)
                        Toast.makeText(requireContext(), getString(R.string.transaction_success), Toast.LENGTH_SHORT).show()
                    }, 1500)
                }
                .show()
        }

        binding.epoxyRecyclerViewProductMarketCashierList.setController(totalEpoxyController)
        binding.productMarketEpoxyRecyclerView.setController(epoxyController)

    }

    private fun clearList(list: ArrayList<Product?>, total: ArrayList<Double>, product: List<Product?>){
        list.clear()
        total.clear()
        totalEpoxyController.product = emptyList()
    }

    private fun onItemClicked(pId: String) {
        viewModel.addToCart(pId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}