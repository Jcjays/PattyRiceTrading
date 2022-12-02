package com.patriciajavier.pattyricetrading.home.admin.inventory.order.restock

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Timestamp
import com.patriciajavier.pattyricetrading.MyApp
import com.patriciajavier.pattyricetrading.databinding.FragmentOrderRequestScreenBinding
import com.patriciajavier.pattyricetrading.firestore.models.Order
import com.patriciajavier.pattyricetrading.firestore.models.Product
import com.patriciajavier.pattyricetrading.firestore.models.Response
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.collect

class OrderRequestScreen :  BottomSheetDialogFragment() {

    private var _binding: FragmentOrderRequestScreenBinding? = null
    private val binding get() = _binding!!

    private val args : OrderRequestScreenArgs by navArgs()
    private val viewModel : OrderRequestViewModel by activityViewModels()

    private var product : Product? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderRequestScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        args.pId.let {
            viewModel.getProductFromFireStore(it)
        }

        //display the product
        viewModel.getProductLiveData.observe(viewLifecycleOwner){ response ->
            when(response){
                is Response.Loading -> {}
                is Response.Failure -> Toast.makeText(requireContext(), response.e.message.toString(), Toast.LENGTH_SHORT).show()
                is Response.Success -> {

                    product = response.data

                    Glide.with(requireContext())
                        .load(response.data!!.productImage)
                        .into(binding.riceImage)

                    binding.riceTitle.text = response.data.productName
                    binding.riceStockCount.text = "Stocks left: ${response.data.stock}"
                    binding.riceVariation.text = "Variation: ${response.data.kiloPerSack} kg"
                    binding.riceUnitPrice.text = "P${response.data.unitPrice}"
                }
            }
        }


        //observe the event when users ordered
        lifecycleScope.launchWhenStarted {
            viewModel.eventFLow.collect { response ->
                when (response) {
                    is Response.Loading -> {}
                    is Response.Failure -> Toast.makeText(
                        requireContext(),
                        response.e.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    is Response.Success -> {
                        Toast.makeText(
                            requireContext(),
                            response.data.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }


            var count = 1
            binding.editText.doOnTextChanged { text, start, before, _c ->
                binding.totalCost.text = "Total: ${product!!.unitPrice * count}"
            }

            binding.buttonIncreaseQty.setOnClickListener {
                if(count >= product!!.stock){
                    return@setOnClickListener
                }

                binding.editText.setText("${++count}")
            }

            binding.buttonDecreaseQty.setOnClickListener {
                if(count <= 0 ){
                    return@setOnClickListener
                }

                binding.editText.setText("${--count}")
            }


            binding.submitOrder.setOnClickListener {
                if(count == 0)
                    return@setOnClickListener

                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Confirm Order?")
                    .setMessage("Please review all the information you cannot UNDO this operation.")
                    .setNegativeButton("Cancel") { dialog, which ->
                        dialog.dismiss()
                    }
                    .setPositiveButton("Proceed") { dialog, which ->
                        val order = Order(
                            pId = product!!.pId,
                            uId = MyApp.userId,
                            qTy = count,
                            variation = product!!.kiloPerSack,
                            totalCost = product!!.unitPrice * count
                        )

                        viewModel.setOrderRequest(order)
                        findNavController().navigateUp()
                    }
                    .show()
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}