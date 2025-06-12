package com.example.birthdaytrackerv2.ui.add

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.birthdaytrackerv2.R
import com.example.birthdaytrackerv2.ReminderManager.deleteAlarm
import com.example.birthdaytrackerv2.ReminderManager.setAlarm
import com.example.birthdaytrackerv2.databinding.FragmentAddBirthdayBinding
import com.example.birthdaytrackerv2.ui.data.BirthdayAdapter
import com.example.birthdaytrackerv2.ui.data.BirthdayNote
import com.example.birthdaytrackerv2.ui.data.MyDatabase
import java.text.SimpleDateFormat
import java.util.Locale

class AddBirthdayFragment : Fragment() {

    private var _binding: FragmentAddBirthdayBinding? = null
    private val binding get() = _binding!!
    private val REQUEST_CODE_POST_NOTIFICATIONS = 1

    private lateinit var dbHelper: MyDatabase
    private lateinit var items: ArrayList<BirthdayNote>
    private lateinit var cursor: Cursor
    private lateinit var adapter: BirthdayAdapter

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
            val date_day =
                cursor.getString(cursor.getColumnIndexOrThrow(MyDatabase.BIRTHDAY_DATE_DAY))
            val date_month =
                cursor.getString(cursor.getColumnIndexOrThrow(MyDatabase.BIRTHDAY_DATE_MONTH))
            val date_year: String? =
                cursor.getString(cursor.getColumnIndexOrThrow(MyDatabase.BIRTHDAY_DATE_YEAR))

            items.add(BirthdayNote(id, name, date_day, date_month, date_year))
        }
        sortItems()

        _binding = FragmentAddBirthdayBinding.inflate(inflater, container, false)
        val root: View = binding.root

        adapter = BirthdayAdapter(requireContext(), items) { position ->
            val itemToDelete = items[position].id
            dbHelper.deleteData(itemToDelete)

            deleteAlarm(requireContext(), itemToDelete.toInt())

            items.removeAt(position)
            sortItems()
            adapter.notifyDataSetChanged()
        }

        val listView: ListView = binding.listView
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = items[position]
            Toast.makeText(requireContext(), "Clicked: ${selectedItem.name}", Toast.LENGTH_SHORT)
                .show()
        }

        val addButton: ImageButton = binding.addButton
        addButton.setOnClickListener {
            showInputDialog(requireContext())
        }

        return root
    }

    private fun showInputDialog(context: Context) {
        val container = layoutInflater.inflate(R.layout.layout_add_birthday, null)

        val inputName = container.findViewById<EditText>(R.id.add_name_edit_text)
        val inputDate = container.findViewById<EditText>(R.id.add_date_edit_text)
        val cancelBtn = container.findViewById<Button>(R.id.cancel_button)
        val okBtn = container.findViewById<Button>(R.id.ok_button)

        //TODO Изменить дизайн
        val dialog = AlertDialog.Builder(context)
            .setTitle("Добавление")
            .setMessage("Заполните имя и дату:")
            .setView(container)
            .create()

        dialog.show()

        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        okBtn.setOnClickListener {
            if (checkIsCorrectName(inputName.text.toString())) {
                if (checkIsCorrectDate(inputDate.text.toString())) {
                    val dateList: List<String> = inputDate.text.toString().split(".")
                    val resultDateList = concatDate(dateList)

                    val id = dbHelper.insertData(
                        inputName.text.toString(),
                        resultDateList[0]!!,
                        resultDateList[1]!!,
                        resultDateList[2]
                    )
                    items.add(
                        BirthdayNote (
                            id,
                            inputName.text.toString(),
                            resultDateList[0]!!,
                            resultDateList[1]!!,
                            resultDateList[2]
                        )
                    )
                    sortItems()
                    adapter.notifyDataSetChanged()

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_CODE_POST_NOTIFICATIONS)
                        }
                    }

                    setAlarm(requireContext(), dateList[0].toInt(), dateList[1].toInt(), id.toInt(), inputName.text.toString())

                    dialog.dismiss()
                } else {
                    Toast.makeText(context, "Неправильно введенная дата!", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(context, "Неправильно введенное имя!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun concatDate(dateList: List<String>): MutableList<String?> {
        val resultList = MutableList<String?>(3) { null }
        val concatString = StringBuilder()

        if (dateList[0].length == 1) {
            concatString.append("0")
        }
        concatString.append(dateList[0])
        resultList[0] = concatString.toString()
        concatString.clear()

        if (dateList[1].length == 1) {
            concatString.append("0")
        }
        concatString.append(dateList[1])
        resultList[1] = concatString.toString()

        return resultList
    }

    private fun checkIsCorrectName(name: String): Boolean {
        return name.matches(Regex("^[а-яА-Яa-zA-Z ]+$"))
    }

    private fun checkIsCorrectDate(date: String): Boolean {
        var dateList: List<String>

        try {
            SimpleDateFormat("dd.mm.yyyy", Locale.getDefault()).parse(date)
            dateList = date.split(".")
            if (Integer.parseInt(dateList[2]) !in 1000..4000) {
                return false
            }
        } catch (e: Exception) {
            try {
                SimpleDateFormat("dd.mm", Locale.getDefault()).parse(date)
                dateList = date.split(".")
            } catch (e: Exception) {
                return false
            }
        }

        val monthNumber: Int
        val dayNumber: Int
        val days30list = listOf(4, 6, 9, 11)

        try {
            monthNumber = Integer.parseInt(dateList[1])
            dayNumber = Integer.parseInt(dateList[0])
        } catch (e: Exception) {
            return false
        }

        if (monthNumber !in 1..12) {
            return false
        }

        if (dayNumber !in 1..28) {
            if (monthNumber == 2) {
                return false
            }

            if (dayNumber !in 29..30) {
                if (monthNumber in days30list) {
                    return false
                }

                if (dayNumber != 31) {
                    return false
                }
            }
        }

        return true
    }

    private fun sortItems() {
        val sortedItemsList = items.sortedWith(compareBy({ it.date_month }, { it.date_day }))
        var i = 0

        for (el in sortedItemsList) {
            items[i++] = el
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        cursor.close()
    }
}