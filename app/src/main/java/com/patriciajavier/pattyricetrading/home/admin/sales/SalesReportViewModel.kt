package com.patriciajavier.pattyricetrading.home.admin.sales

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patriciajavier.pattyricetrading.Constant
import com.patriciajavier.pattyricetrading.firestore.FirebaseService
import com.patriciajavier.pattyricetrading.firestore.models.Logs
import com.patriciajavier.pattyricetrading.firestore.models.Response
import com.patriciajavier.pattyricetrading.formatWithPattern
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.Month
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.TemporalAdjusters.*

class SalesReportViewModel: ViewModel() {

    private val _fullCopyOfLogsMutableLiveData = MutableLiveData<List<Logs>>()

    private val _logsResponseMutableLiveData = MutableLiveData<LogsResponseState>()
    val logsResponseStateLiveData : LiveData<LogsResponseState> get() = _logsResponseMutableLiveData

    fun getAdminLogs() = viewModelScope.launch {
        FirebaseService.getAdminLogs().collect{ response ->
            when(response){
                Response.Loading -> _logsResponseMutableLiveData.postValue(
                    LogsResponseState(isLoadingDone = false)
                )
                is Response.Failure -> _logsResponseMutableLiveData.postValue(
                    LogsResponseState(errorMessage = response.e.message)
                )
                is Response.Success ->{
                    _logsResponseMutableLiveData.postValue(
                        LogsResponseState(logs = response.data!!)
                    )

                    _fullCopyOfLogsMutableLiveData.postValue(response.data)
                }
            }
        }
    }

    fun getShopkeeperLogs(uId: String) = viewModelScope.launch {
        FirebaseService.getShopkeeperLogs(uId).collect { response ->
            when(response){
                Response.Loading -> _logsResponseMutableLiveData.postValue(
                    LogsResponseState(isLoadingDone = false)
                )
                is Response.Failure -> _logsResponseMutableLiveData.postValue(
                    LogsResponseState(errorMessage = response.e.message)
                )
                is Response.Success -> {
                    _logsResponseMutableLiveData.postValue(
                        LogsResponseState(logs = response.data!!)
                    )

                    _fullCopyOfLogsMutableLiveData.postValue(response.data)
                }
            }
        }
    }


    fun filterTransactionRecord(
        dayFilterState: DayFilterState = DayFilterState(),
        weeklyFilterState: WeeklyFilterState = WeeklyFilterState(),
        monthlyFilterState: MonthlyFilterState = MonthlyFilterState(),
        yearlyFilterState: YearlyFilterState = YearlyFilterState()
    ){
        if(_fullCopyOfLogsMutableLiveData.value != null){

            val constant = Constant

            var listOfFilteredLogs = ArrayList<Logs>()
            var currentDate = ZonedDateTime.now(ZoneId.of("Asia/Hong_Kong"))

            var dateToDisplay = currentDate.formatWithPattern(constant.formatYear)

            var firstDay : ZonedDateTime
            var lastDay : ZonedDateTime

            if(yearlyFilterState.date.year <= currentDate.year){
                val newCurrentDate = (currentDate.year - yearlyFilterState.date.year)

                currentDate = currentDate.minusYears(newCurrentDate.toLong())
                firstDay = currentDate.with(firstDayOfYear())
                lastDay = currentDate.with(lastDayOfYear())
                dateToDisplay = yearlyFilterState.date.formatWithPattern(constant.formatYear)

                //if the transaction is within the year add it all to the list
                _fullCopyOfLogsMutableLiveData.value!!.sortedByDescending { it.timeCreated }.forEach {
                    if(firstDay.formatWithPattern(constant.formatYearMonthDay) <= Constant.timeStampToGMT8(it.timeCreated, constant.formatYearMonthDay)
                        && Constant.timeStampToGMT8(it.timeCreated, constant.formatYearMonthDay) <= lastDay.formatWithPattern(constant.formatYearMonthDay)){

                        listOfFilteredLogs.add(it)

                    }
                }
            }

            /**
             *  Monthly filters
             */

            if(monthlyFilterState.january){
                currentDate = currentDate.with(Month.JANUARY)
                firstDay = currentDate.with(firstDayOfMonth())
                lastDay = currentDate.with(lastDayOfMonth())
                dateToDisplay = currentDate.formatWithPattern(constant.formatYearMonth)

                listOfFilteredLogs = addLogsWithinSpecifiedRange(firstDay, lastDay)
            }

            if(monthlyFilterState.february){
                currentDate = currentDate.with(Month.FEBRUARY)
                firstDay = currentDate.with(firstDayOfMonth())
                lastDay = currentDate.with(lastDayOfMonth())
                dateToDisplay = currentDate.formatWithPattern(constant.formatYearMonth)

                listOfFilteredLogs = addLogsWithinSpecifiedRange(firstDay, lastDay)
            }

            if(monthlyFilterState.march){
                currentDate = currentDate.with(Month.MARCH)
                firstDay = currentDate.with(firstDayOfMonth())
                lastDay = currentDate.with(lastDayOfMonth())
                dateToDisplay = currentDate.formatWithPattern(constant.formatYearMonth)

                listOfFilteredLogs = addLogsWithinSpecifiedRange(firstDay, lastDay)
            }

            if(monthlyFilterState.april){
                currentDate = currentDate.with(Month.APRIL)
                firstDay = currentDate.with(firstDayOfMonth())
                lastDay = currentDate.with(lastDayOfMonth())
                dateToDisplay = currentDate.formatWithPattern(constant.formatYearMonth)

                listOfFilteredLogs = addLogsWithinSpecifiedRange(firstDay, lastDay)
            }

            if(monthlyFilterState.may){
                currentDate = currentDate.with(Month.MAY)
                firstDay = currentDate.with(firstDayOfMonth())
                lastDay = currentDate.with(lastDayOfMonth())
                dateToDisplay = currentDate.formatWithPattern(constant.formatYearMonth)

                listOfFilteredLogs = addLogsWithinSpecifiedRange(firstDay, lastDay)
            }

            if(monthlyFilterState.june){
                currentDate = currentDate.with(Month.JUNE)
                firstDay = currentDate.with(firstDayOfMonth())
                lastDay = currentDate.with(lastDayOfMonth())
                dateToDisplay = currentDate.formatWithPattern(constant.formatYearMonth)

                listOfFilteredLogs = addLogsWithinSpecifiedRange(firstDay, lastDay)
            }

            if(monthlyFilterState.july){
                currentDate = currentDate.with(Month.JULY)
                firstDay = currentDate.with(firstDayOfMonth())
                lastDay = currentDate.with(lastDayOfMonth())
                dateToDisplay = currentDate.formatWithPattern(constant.formatYearMonth)

                listOfFilteredLogs = addLogsWithinSpecifiedRange(firstDay, lastDay)
            }

            if(monthlyFilterState.august){
                currentDate = currentDate.with(Month.AUGUST)
                firstDay = currentDate.with(firstDayOfMonth())
                lastDay = currentDate.with(lastDayOfMonth())
                dateToDisplay = currentDate.formatWithPattern(constant.formatYearMonth)

                listOfFilteredLogs = addLogsWithinSpecifiedRange(firstDay, lastDay)
            }

            if(monthlyFilterState.september){
                currentDate = currentDate.with(Month.SEPTEMBER)
                firstDay = currentDate.with(firstDayOfMonth())
                lastDay = currentDate.with(lastDayOfMonth())
                dateToDisplay = currentDate.formatWithPattern(constant.formatYearMonth)

                listOfFilteredLogs = addLogsWithinSpecifiedRange(firstDay, lastDay)
            }

            if(monthlyFilterState.october){
                currentDate = currentDate.with(Month.OCTOBER)
                firstDay = currentDate.with(firstDayOfMonth())
                lastDay = currentDate.with(lastDayOfMonth())
                dateToDisplay = currentDate.formatWithPattern(constant.formatYearMonth)

                listOfFilteredLogs = addLogsWithinSpecifiedRange(firstDay, lastDay)
            }

            if(monthlyFilterState.november){
                currentDate = currentDate.with(Month.NOVEMBER)
                firstDay = currentDate.with(firstDayOfMonth())
                lastDay = currentDate.with(lastDayOfMonth())
                dateToDisplay = currentDate.formatWithPattern(constant.formatYearMonth)

                listOfFilteredLogs = addLogsWithinSpecifiedRange(firstDay, lastDay)
            }

            if(monthlyFilterState.december){
                currentDate = currentDate.with(Month.DECEMBER)
                firstDay = currentDate.with(firstDayOfMonth())
                lastDay = currentDate.with(lastDayOfMonth())
                dateToDisplay = currentDate.formatWithPattern(constant.formatYearMonth)

                listOfFilteredLogs = addLogsWithinSpecifiedRange(firstDay, lastDay)
            }

            /**
             * Weekly filters
             */

            if(weeklyFilterState.week1){
                firstDay = currentDate.with(firstDayOfMonth())
                lastDay = currentDate.withDayOfMonth(7)
                currentDate = lastDay
                dateToDisplay = currentDate.formatWithPattern(constant.formatYearMonth) + "${firstDay.formatWithPattern("dd")}-7"

                listOfFilteredLogs = addLogsWithinSpecifiedRange(firstDay, lastDay)
            }

            if(weeklyFilterState.week2){
                firstDay = currentDate.withDayOfMonth(8)
                lastDay = currentDate.withDayOfMonth(14)
                currentDate = lastDay
                dateToDisplay = currentDate.formatWithPattern(constant.formatYearMonth) + "8-14"

                listOfFilteredLogs = addLogsWithinSpecifiedRange(firstDay, lastDay)
            }

            if(weeklyFilterState.week3){
                firstDay = currentDate.withDayOfMonth(15)
                lastDay = currentDate.withDayOfMonth(22)
                currentDate = lastDay
                dateToDisplay = currentDate.formatWithPattern(constant.formatYearMonth) + "15-22"

                listOfFilteredLogs = addLogsWithinSpecifiedRange(firstDay, lastDay)
            }

            if(weeklyFilterState.week4){
                firstDay = currentDate.withDayOfMonth(23)
                lastDay = currentDate.with(lastDayOfMonth())
                currentDate = lastDay
                dateToDisplay = currentDate.formatWithPattern(constant.formatYearMonth) + "23-${lastDay.formatWithPattern("dd")}"

                listOfFilteredLogs = addLogsWithinSpecifiedRange(firstDay, lastDay)
            }

            /**
             *  Day filters
             */

            if (dayFilterState.day1){
                currentDate = currentDate.minusWeeks(1).with(DayOfWeek.MONDAY)
                listOfFilteredLogs = addLogsIfMatch(
                    currentDate
                )
                dateToDisplay = currentDate.formatWithPattern(constant.formatYearMonthDay)
            }

            if (dayFilterState.day2){
                currentDate = currentDate.minusWeeks(1).with(DayOfWeek.TUESDAY)
                listOfFilteredLogs = addLogsIfMatch(
                    currentDate
                )
                dateToDisplay = currentDate.formatWithPattern(constant.formatYearMonthDay)
            }

            if (dayFilterState.day3){
                currentDate = currentDate.minusWeeks(1).with(DayOfWeek.WEDNESDAY)
                listOfFilteredLogs = addLogsIfMatch(
                    currentDate
                )
                dateToDisplay = currentDate.formatWithPattern(constant.formatYearMonthDay)
            }

            if (dayFilterState.day4){
                currentDate = currentDate.minusWeeks(1).with(DayOfWeek.THURSDAY)
                listOfFilteredLogs = addLogsIfMatch(
                    currentDate
                )
                dateToDisplay = currentDate.formatWithPattern(constant.formatYearMonthDay)
            }
            if (dayFilterState.day5){
                currentDate = currentDate.minusWeeks(1).with(DayOfWeek.FRIDAY)
                listOfFilteredLogs = addLogsIfMatch(
                    currentDate
                )
                dateToDisplay = currentDate.formatWithPattern(constant.formatYearMonthDay)
            }

            if (dayFilterState.day6){
                currentDate = currentDate.minusWeeks(1).with(DayOfWeek.SATURDAY)
                listOfFilteredLogs = addLogsIfMatch(
                    currentDate
                )
                dateToDisplay = currentDate.formatWithPattern(constant.formatYearMonthDay)
            }

            if (dayFilterState.day7){
                currentDate = currentDate.minusWeeks(1).with(DayOfWeek.SUNDAY)
                listOfFilteredLogs = addLogsIfMatch(
                    currentDate
                )
                dateToDisplay = currentDate.formatWithPattern(constant.formatYearMonthDay)
            }

            Log.d("value", listOfFilteredLogs.toString())

            _logsResponseMutableLiveData.postValue(LogsResponseState(logs = listOfFilteredLogs, dateToDisplay = dateToDisplay))
        }
    }


    /**
     * Helper function below
     */

    private fun addLogsWithinSpecifiedRange(firstDay : ZonedDateTime, lastDay : ZonedDateTime): ArrayList<Logs> {
        val format = "yyyyMMdd"
        val newFilteredLogs = ArrayList<Logs>()

        _fullCopyOfLogsMutableLiveData.value!!.sortedByDescending { it.timeCreated }.forEach {
            if(firstDay.formatWithPattern(format) <= Constant.timeStampToGMT8(it.timeCreated, format)
                && Constant.timeStampToGMT8(it.timeCreated, format) <= lastDay.formatWithPattern(format)){
                newFilteredLogs.add(it)
            }
        }
        return newFilteredLogs
    }

    private fun addLogsIfMatch(filterDate : ZonedDateTime): ArrayList<Logs> {
        val format = "yyyyMMdd"
        val newFilteredLogs = ArrayList<Logs>()

        _fullCopyOfLogsMutableLiveData.value!!.sortedByDescending { it.timeCreated }.forEach {
            if(filterDate.formatWithPattern(format) == Constant.timeStampToGMT8(it.timeCreated, format))
                newFilteredLogs.add(it)
        }
        return newFilteredLogs
    }
}


/**
 * State classes
 */


data class LogsResponseState(
    val logs : List<Logs> = emptyList(),
    val isLoadingDone : Boolean = true,
    val errorMessage : String? = null,
    val dateToDisplay : String = ZonedDateTime.now(ZoneId.of("Asia/Hong_Kong")).formatWithPattern(Constant.formatYear)
)

enum class DayFilterOptions{
    Mon, Tue, Wed, Thu, Fri, Sat, Sun;

    companion object{
        fun getDays() : ArrayList<String>{
            val listOfDays = ArrayList<String>()
            for (day in values()){
                listOfDays.add(day.name)
            }

            return listOfDays
        }

    }
}

enum class WeekFilterOptions{
    Week1, Week2, Week3, Week4;

    companion object{
        fun getWeeks() : ArrayList<String>{
            val listWeeks = ArrayList<String>()
            for (week in values()){
                listWeeks.add(week.name)
            }

            return listWeeks
        }
    }
}

enum class MonthFilterOption{
    Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec;

    companion object{
        fun getMonths() : ArrayList<String>{
            val listMonths = ArrayList<String>()
            for (month in values()){
                listMonths.add(month.name)
            }

            return listMonths
        }
    }
}

data class WeeklyFilterState(
    val week1 : Boolean = false,
    val week2 : Boolean = false,
    val week3 : Boolean = false,
    val week4 : Boolean = false
)

data class DayFilterState(
    val day1 : Boolean = false,
    val day2 : Boolean = false,
    val day3 : Boolean = false,
    val day4 : Boolean = false,
    val day5 : Boolean = false,
    val day6 : Boolean = false,
    val day7 : Boolean = false
)

data class MonthlyFilterState(
    val january : Boolean = false,
    val february : Boolean = false,
    val march : Boolean = false,
    val april : Boolean = false,
    val may : Boolean = false,
    val june : Boolean = false,
    val july : Boolean = false,
    val august : Boolean = false,
    val september : Boolean = false,
    val october : Boolean = false,
    val november : Boolean = false,
    val december : Boolean = false,
)

data class YearlyFilterState(
    val date : ZonedDateTime = ZonedDateTime.now(ZoneId.of("Asia/Hong_Kong"))
)


