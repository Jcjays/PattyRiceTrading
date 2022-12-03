package com.patriciajavier.pattyricetrading.home.admin.market

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
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
        var sum: Double= 0.0
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

                    sum= 0.0
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
            sum = 0.0
            binding.paymentOutlinedEditText.setText("")
        }

        binding.sellProductsButton.setOnClickListener {
            var inputEditTextField = EditText(requireActivity())
            var dialog = AlertDialog.Builder(requireContext())
                .setTitle("Confirm order?")
                .setMessage("Input customer payment")
                .setView(inputEditTextField)
                .setNegativeButton("Cancel") { dialog, which ->
                    dialog.dismiss()
                }

                .setPositiveButton("Confirm") { dialog, which ->
                    viewModel.sellProductToCustomer(MyApp.userId, product)
                    clearList(list,total,totalEpoxyController.product)
                    product.clear()

                    if(inputEditTextField.text.trim().isEmpty()){
                        Toast.makeText(requireContext(),"No payment received. Input payment", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        viewModel.sellProductToCustomer(MyApp.userId, product).cancel()
                    }
                    else if(inputEditTextField.text.isDigitsOnly().not()){
                    Toast.makeText(requireContext(),"Invalid input. Input number", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    viewModel.sellProductToCustomer(MyApp.userId, product).cancel()
                }
                    else if(inputEditTextField.text.toString().toDouble()<sum){
                        Toast.makeText(requireContext(),"Customer payment not enough", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        viewModel.sellProductToCustomer(MyApp.userId, product).cancel()
                    }
                    else{
                        var paymentTotal: Double = inputEditTextField.text.toString().toDouble()
                        var customerChange= paymentTotal.minus(sum)
                        binding.paymentOutlinedEditText.setText("")
                        binding.loadingState.root.isVisible = true
                        Toast.makeText(requireContext(), "Customer change$customerChange", Toast.LENGTH_LONG).show()
                        Handler(Looper.getMainLooper()).postDelayed({
                            binding.loadingState.root.isGone = true
                            findNavController().navigate(R.id.action_productMarketScreen_self)
                            Toast.makeText(requireContext(), getString(R.string.transaction_success), Toast.LENGTH_SHORT).show()
                        }, 1500)
                    }

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
    private fun matches(regex: String): Boolean {
    return true
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

