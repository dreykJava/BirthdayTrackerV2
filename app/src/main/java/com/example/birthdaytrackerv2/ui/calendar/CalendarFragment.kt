package com.example.birthdaytrackerv2.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.birthdaytrackerv2.databinding.FragmentCalendarBinding
import com.example.birthdaytrackerv2.ui.calendarview.NewCustomCalendarView
import com.example.birthdaytrackerv2.ui.data.BirthdayNote
import com.example.birthdaytrackerv2.ui.data.SharedViewModel

class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    private lateinit var customCalendarView: NewCustomCalendarView
    private lateinit var previousButton: Button
    private lateinit var nextButton: Button
    private lateinit var selectedMonth: TextView
    private lateinit var selectedYear: TextView
    private lateinit var selectedBirthday: TextView

    private val monthsList = listOf("Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
        "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь")

    private lateinit var items: ArrayList<BirthdayNote>
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        sharedViewModel.sharedData.observe(viewLifecycleOwner) { data ->
            items = data
        }

        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        val root: View = binding.root

        selectedBirthday = binding.selectedBirthday
        //TODO Для календаря сделать статичный размер, чтобы можно было распологать элементы снизу
        customCalendarView = binding.myCalendarView
        //customCalendarView.setItemsFromDb(items)
        previousButton = binding.previousMonthBtn
        nextButton = binding.nextMonthBtn

        val currentSelectedMonth = customCalendarView.getMonth()
        val currentSelectedYear = customCalendarView.getYear()
        selectedMonth = binding.currentSelectedMonthTv
        selectedYear = binding.currentSelectedYearTv
        selectedMonth.text = monthsList[currentSelectedMonth]
        selectedYear.text = currentSelectedYear.toString()

        previousButton.setOnClickListener {
            customCalendarView.changeMonth(-1)
            updateMonthDisplay()
        }

        nextButton.setOnClickListener {
            customCalendarView.changeMonth(1)
            updateMonthDisplay()
        }

        return root
    }

    private fun updateMonthDisplay() {
        selectedMonth.text = monthsList[customCalendarView.getMonth()]
        selectedYear.text = customCalendarView.getYear().toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}