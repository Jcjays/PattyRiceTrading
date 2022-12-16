package com.patriciajavier.pattyricetrading.home.admin.sales

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.patriciajavier.pattyricetrading.Constant
import com.patriciajavier.pattyricetrading.MyApp
import com.patriciajavier.pattyricetrading.R
import com.patriciajavier.pattyricetrading.databinding.FragmentSalesReportScreenBinding
import com.patriciajavier.pattyricetrading.firestore.models.Logs
import java.time.ZoneId
import java.time.ZonedDateTime

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
        initFilter()

    }

    private fun initFilter() {
        binding.filterLabel.setOnClickListener {
            binding.linearLayoutCompat.isGone = !binding.linearLayoutCompat.isGone
        }

        binding.cancel.setOnClickListener {
            viewModel.filterTransactionRecord(
                DayFilterState(),
                WeeklyFilterState(),
                MonthlyFilterState(),
                YearlyFilterState()
            )

            binding.autoCompleteTextViewSortByDay.text = null
            binding.autoCompleteTextViewSortByWeek.text = null
            binding.autoCompleteTextViewSortByMonth.text = null
            binding.autoCompleteTextViewSortByYear.text = null

            binding.linearLayoutCompat.isGone = true
        }


        var dayFilterState = DayFilterState()
        var weekFilterState = WeeklyFilterState()
        var monthFilterState = MonthlyFilterState()
        var yearFilterState = YearlyFilterState()

        binding.apply.setOnClickListener {
            viewModel.filterTransactionRecord(
                dayFilterState,
                weekFilterState,
                monthFilterState,
                yearFilterState
            )
            binding.linearLayoutCompat.isGone = true
        }

        val dayItems = DayFilterOptions.getDays()
        val dayAdapter = ArrayAdapter(requireContext(), R.layout.sort_list_item, dayItems)
        binding.autoCompleteTextViewSortByDay.setAdapter(dayAdapter)

        binding.autoCompleteTextViewSortByDay.doOnTextChanged { text, _, _, _ ->
            dayFilterState = when(text.toString()){
                DayFilterOptions.Mon.name -> DayFilterState(day1 = true)
                DayFilterOptions.Tue.name -> DayFilterState(day2 = true)
                DayFilterOptions.Wed.name -> DayFilterState(day3 = true)
                DayFilterOptions.Thu.name -> DayFilterState(day4 = true)
                DayFilterOptions.Fri.name -> DayFilterState(day5 = true)
                DayFilterOptions.Sat.name -> DayFilterState(day6 = true)
                DayFilterOptions.Sun.name -> DayFilterState(day7 = true)
                else -> DayFilterState()
            }
            Log.d("DayFilter", dayFilterState.toString())
        }

        val weekItems = WeekFilterOptions.getWeeks()
        val weekAdapter = ArrayAdapter(requireContext(), R.layout.sort_list_item, weekItems)
        binding.autoCompleteTextViewSortByWeek.setAdapter(weekAdapter)

        binding.autoCompleteTextViewSortByWeek.doOnTextChanged { text, _, _, _ ->
            weekFilterState = when(text.toString()){
                WeekFilterOptions.Week1.name -> WeeklyFilterState(week1 = true)
                WeekFilterOptions.Week2.name -> WeeklyFilterState(week2 = true)
                WeekFilterOptions.Week3.name -> WeeklyFilterState(week3 = true)
                WeekFilterOptions.Week4.name -> WeeklyFilterState(week4 = true)
                else -> WeeklyFilterState()
            }
            Log.d("DayFilter", weekFilterState.toString())
        }

        val monthItems = MonthFilterOption.getMonths()
        val monthAdapter = ArrayAdapter(requireContext(), R.layout.sort_list_item, monthItems)
        binding.autoCompleteTextViewSortByMonth.setAdapter(monthAdapter)

        binding.autoCompleteTextViewSortByMonth.doOnTextChanged { text, _, _, _ ->
            monthFilterState = when(text.toString()){
                MonthFilterOption.Jan.name -> MonthlyFilterState(january = true)
                MonthFilterOption.Feb.name -> MonthlyFilterState(february = true)
                MonthFilterOption.Mar.name -> MonthlyFilterState(march = true)
                MonthFilterOption.Apr.name -> MonthlyFilterState(april = true)
                MonthFilterOption.May.name -> MonthlyFilterState(may = true)
                MonthFilterOption.Jun.name -> MonthlyFilterState(june = true)
                MonthFilterOption.Jul.name -> MonthlyFilterState(july = true)
                MonthFilterOption.Aug.name -> MonthlyFilterState(august = true)
                MonthFilterOption.Sep.name -> MonthlyFilterState(september = true)
                MonthFilterOption.Oct.name -> MonthlyFilterState(october = true)
                MonthFilterOption.Nov.name -> MonthlyFilterState(november = true)
                MonthFilterOption.Dec.name -> MonthlyFilterState(december = true)
                else -> MonthlyFilterState()
            }
            Log.d("DayFilter", monthFilterState.toString())
        }

        val yearItems = Constant.getListOfDatesFromOrigin()
        val yearAdapter = ArrayAdapter(requireContext(), R.layout.sort_list_item, yearItems)
        binding.autoCompleteTextViewSortByYear.setAdapter(yearAdapter)

        binding.autoCompleteTextViewSortByYear.doOnTextChanged { text, _, _, _ ->

            if(text.isNullOrBlank()){
                yearFilterState = YearlyFilterState()
                return@doOnTextChanged
            }

            //start the year from january 1st,
            val zoneDateTimeBaseOnYearSelected = ZonedDateTime.of(
                Integer.parseInt(text.toString()),
                1,
                1,
                0,
                0,
                0,
                0,
                ZoneId.of("Asia/Hong_Kong"))

            yearFilterState = YearlyFilterState(zoneDateTimeBaseOnYearSelected)
            Log.d("DayFilter", yearFilterState.toString())
        }

    }

    private fun initObservables() {
        viewModel.logsResponseStateLiveData.observe(viewLifecycleOwner) { logsResponseState ->
            binding.loadingState.root.isGone = logsResponseState.isLoadingDone

            if (!logsResponseState.errorMessage.isNullOrBlank()) {
                Toast.makeText(requireContext(), logsResponseState.errorMessage, Toast.LENGTH_SHORT).show()
                return@observe
            }


            if(logsResponseState.dateToDisplay.length <= 3){
                val year = logsResponseState.dateToDisplay.substring(0)
                binding.transactionDateLabel.text = "Transactions : $year"
            }

            if(logsResponseState.dateToDisplay.length in 4..6){
                val year = logsResponseState.dateToDisplay.substring(0, 4)
                val month = logsResponseState.dateToDisplay.substring(4)

                binding.transactionDateLabel.text = "Transactions : $year-$month"
            }


            if(logsResponseState.dateToDisplay.length in 4..logsResponseState.dateToDisplay.length){
                val year = logsResponseState.dateToDisplay.substring(0, 4)
                val month = logsResponseState.dateToDisplay.substring(4, 6)
                val day = logsResponseState.dateToDisplay.substring(6)
                binding.transactionDateLabel.text = "Transactions : $year-$month-$day"
            }

            epoxyController.logs = logsResponseState.logs
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