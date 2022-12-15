package com.patriciajavier.pattyricetrading.home.admin.sales

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.patriciajavier.pattyricetrading.Constant
import com.patriciajavier.pattyricetrading.MyApp
import com.patriciajavier.pattyricetrading.R
import com.patriciajavier.pattyricetrading.databinding.FragmentSalesReportScreenBinding
import com.patriciajavier.pattyricetrading.firestore.models.Logs

class SalesReportScreen : Fragment() {

    private var _binding : FragmentSalesReportScreenBinding? = null
    private val binding get() = _binding!!
    private val viewModel : SalesReportViewModel by activityViewModels()
    private val epoxyController = SalesReportEpoxyController(::onClick)

    private fun onClick(logs: Logs) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(setDialogMessage(logs))
            .setPositiveButton("Ok") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    private fun setDialogMessage(logs: Logs): String{
        val stringBuilder = StringBuilder()

        stringBuilder.append("TransID: ${logs.transactionId}\n")
        stringBuilder.append("ProductName: ${logs.productName}\n")
        stringBuilder.append("CustomerName: ${logs.customerName}\n")
        stringBuilder.append("Time: ${Constant.timeStampToGMT8(logs.timeCreated, Constant.YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)}\n")
        stringBuilder.append("Quantity: ${logs.quantity}\n")
        stringBuilder.append("Variation: ${logs.variation}kg\n")
        stringBuilder.append("Unit cost: ${logs.unitPrice}\n")
        stringBuilder.append("Total: ${logs.totalCost}\n")

        return stringBuilder.toString()
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

        initAccessRights()
        initObservables()
        initViews()


    }

    private fun initViews() {
        val items = SalesReportViewModel.SalesSortedViewState.Sort.values()
        val adapter = ArrayAdapter(requireContext(), R.layout.sort_list_item, items)
        binding.autoCompleteTextView.setAdapter(adapter)

        //sort the data base on user wants.
        binding.autoCompleteTextView.doOnTextChanged { text, _, _, _ ->
            viewModel.currentSort = items.find { it.name == text.toString() }!!
        }

        /// search date via date picker?
        val dateShown = binding.DateShown
        binding.SearchDate.setOnClickListener(){
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, y, m, dayOfMonth ->
                    dateShown.text = ("$dayOfMonth-${m+1}-$y")
                },
                year,
                month,
                day
            )

            datePickerDialog.show()
        }

    }

    private fun initObservables() {
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

        binding.salesReportScreenEpoxyRecyclerView.setController(epoxyController)
    }

    private fun initAccessRights() {
        if (MyApp.accessRights)
            viewModel.getAdminLogs()
        else
            viewModel.getShopkeeperLogs(MyApp.userId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}