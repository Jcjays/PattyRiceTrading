package com.patriciajavier.pattyricetrading.home.admin.sales

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.patriciajavier.pattyricetrading.Constant
import com.patriciajavier.pattyricetrading.MyApp
import com.patriciajavier.pattyricetrading.R
import com.patriciajavier.pattyricetrading.databinding.FragmentSalesReportScreenBinding
import com.patriciajavier.pattyricetrading.firestore.models.Logs
import com.patriciajavier.pattyricetrading.firestore.models.Response

class SalesReportScreen : Fragment() {

    private var _binding : FragmentSalesReportScreenBinding? = null
    private val binding get() = _binding!!
    private val viewModel : SalesReportViewModel by activityViewModels()
    private val epoxyController = SalesReportEpoxyController(::onClick)

    private fun onClick(logs: Logs) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage("""
TransID: ${logs.transactionId}
ProductName: ${logs.productName}
CustomerName: ${logs.customerName}
Time: ${Constant.timeStampToGMT8(logs.timeCreated, Constant.YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)}
Quantity: ${logs.quantity}
Variation: ${logs.variation}kg
Unit cost: ${logs.unitPrice}
Total: ${logs.totalCost}              
""")
            .setPositiveButton("Ok") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSalesReportScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (MyApp.accessRights)
            viewModel.getAdminLogs()
        else
            viewModel.getShopkeeperLogs(MyApp.userId)


        viewModel.updateViewStateLiveData.observe(viewLifecycleOwner) {
            if (it.exception != null) {
                Toast.makeText(requireContext(), it.exception.message, Toast.LENGTH_SHORT).show()
                return@observe
            }

            binding.loadingState.root.isVisible = it.isLoading

            if (it.data.isNotEmpty()) {
                epoxyController.logs = it.data
            }
        }

        //provide the data needed for sorting values
        val items = SalesReportViewModel.SalesSortedViewState.Sort.values()
        val adapter = ArrayAdapter(requireContext(), R.layout.sort_list_item, items)
        binding.autoCompleteTextView.setAdapter(adapter)

        //sort the data base on user wants.
        binding.autoCompleteTextView.doOnTextChanged { text, _, _, _ ->
            viewModel.currentSort = items.find { it.name == text.toString() }!!
        }

        binding.salesReportScreenEpoxyRecyclerView.setController(epoxyController)

        //PDF generation
        binding.DownloadPDFbutton.setOnClickListener{
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Download Sales Report PDF")
                .setMessage("Make sure the correct Data is being shown on screen for PDF download")
                .setNegativeButton("Cancel") { dialog, which ->
                    dialog.dismiss()
                }
                .setPositiveButton("Confirm") { dialog, which ->
                    //change to download pdf method?
                    GeneratePDF()
                    Toast.makeText(requireContext(), "Downloading", Toast.LENGTH_SHORT).show()
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