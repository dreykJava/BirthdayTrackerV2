package com.example.birthdaytrackerv2.ui.home

import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.birthdaytrackerv2.databinding.FragmentHomeBinding
import com.example.birthdaytrackerv2.ui.data.BirthdayNote
import com.example.birthdaytrackerv2.ui.data.MyDatabase
import com.example.birthdaytrackerv2.ui.data.SharedViewModel
import java.util.Calendar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: MyDatabase
    private lateinit var items: ArrayList<BirthdayNote>
    private lateinit var cursor: Cursor
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dbHelper = MyDatabase(requireContext())
        items = ArrayList()

        cursor = dbHelper.readData()
        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(MyDatabase.ID))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(MyDatabase.NAME))
            val date_day = cursor.getString(cursor.getColumnIndexOrThrow(MyDatabase.BIRTHDAY_DATE_DAY))
            val date_month = cursor.getString(cursor.getColumnIndexOrThrow(MyDatabase.BIRTHDAY_DATE_MONTH))
            val date_year: String? = cursor.getString(cursor.getColumnIndexOrThrow(MyDatabase.BIRTHDAY_DATE_YEAR))

            items.add(BirthdayNote(id, name, date_day, date_month, date_year))
        }

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        sharedViewModel.setData(items)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val nextBirthdayList = findNextBirthday()

        //TODO Можно объявить глобально и обращаться напрямую к ссылкам в методе
        val nextBirthdayNameTextView: TextView = binding.textNextBirthdayName
        val nextBirthdayTextView: TextView = binding.textNextBirthday

        nextBirthdayNameTextView.text = nextBirthdayList[0]
        nextBirthdayTextView.text = nextBirthdayList[1]

        return root
    }

    //TODO Если получится, перенести метод в AddBirthday и реализовать поиск при добавлении,
    // чтобы не тратить ресурсы при каждом запуске
    //TODO Вынести повторяющийся код в отдельный метод (третий if в цикле)
    //TODO Теряет нули в месяце
    private fun findNextBirthday() : List<String> {
        val currentDate = Calendar.getInstance()
        val currentDay = currentDate.get(Calendar.DAY_OF_MONTH)
        val currentMonth = currentDate.get(Calendar.MONTH) + 1

        var currentItemDay: Int
        var currentItemMonth: Int
        var totalDifference = 500
        var currentDifference: Int

        var nextBirthdayName = "Ни у кого"
        var nextBirthdayDate = ":("
        val stringBuilder = StringBuilder()

        for (el in items) {
            currentItemDay = Integer.parseInt(el.date_day)
            currentItemMonth = Integer.parseInt(el.date_month)

            if (currentItemMonth < currentMonth) {
                currentDifference =  (12 - currentMonth + currentItemMonth) * 31 + (31 - currentDay) + currentItemDay

                if (currentDifference < totalDifference) {
                    stringBuilder.clear()
                    stringBuilder.append(el.date_day)
                    stringBuilder.append(".")
                    stringBuilder.append(el.date_month)

                    nextBirthdayName = el.name
                    nextBirthdayDate = stringBuilder.toString()
                    totalDifference = currentDifference
                } else if (currentDifference == totalDifference) {
                    nextBirthdayName = nextBirthdayName + ", " + el.name
                }
            } else if (currentItemMonth > currentMonth) {
                currentDifference = currentItemDay + (currentItemMonth - currentMonth) * 31 - currentDay

                if (currentDifference < totalDifference) {
                    stringBuilder.clear()
                    stringBuilder.append(el.date_day)
                    stringBuilder.append(".")
                    stringBuilder.append(el.date_month)

                    nextBirthdayName = el.name
                    nextBirthdayDate = stringBuilder.toString()
                    totalDifference = currentDifference
                } else if (currentDifference == totalDifference) {
                    nextBirthdayName = nextBirthdayName + ", " + el.name
                }
            } else {
                if (currentItemDay > currentDay) {
                    currentDifference = currentItemDay - currentDay

                    if (currentDifference < totalDifference) {
                        stringBuilder.clear()
                        stringBuilder.append(el.date_day)
                        stringBuilder.append(".")
                        stringBuilder.append(el.date_month)

                        nextBirthdayName = el.name
                        nextBirthdayDate = stringBuilder.toString()
                        totalDifference = currentDifference
                    } else if (currentDifference == totalDifference) {
                        nextBirthdayName = nextBirthdayName + ", " + el.name
                    }
                } else if (currentItemDay < currentDay) {
                    currentDifference = 365 - (currentDay - currentItemDay)

                    if (currentDifference < totalDifference) {
                        stringBuilder.clear()
                        stringBuilder.append(el.date_day)
                        stringBuilder.append(".")
                        stringBuilder.append(el.date_month)

                        nextBirthdayName = el.name
                        nextBirthdayDate = stringBuilder.toString()
                        totalDifference = currentDifference
                    } else if (currentDifference == totalDifference) {
                        nextBirthdayName = nextBirthdayName + ", " + el.name
                    }
                } else {
                    if (totalDifference == 0) {
                        nextBirthdayName = nextBirthdayName + ", " + el.name
                    } else {
                        totalDifference = 0
                        nextBirthdayName = el.name
                        nextBirthdayDate = "День рождения сегодня!!!"
                    }
                }
            }
        }

        return listOf(nextBirthdayName, nextBirthdayDate)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        cursor.close()
    }
}