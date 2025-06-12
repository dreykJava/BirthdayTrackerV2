package com.example.birthdaytrackerv2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import com.example.birthdaytrackerv2.ui.data.BirthdayNote
import com.example.birthdaytrackerv2.ui.data.MyDatabase
import com.example.birthdaytrackerv2.ReminderManager.setAlarm

class BootCompletedReceiver : BroadcastReceiver() {
    private lateinit var dbHelper: MyDatabase
    private lateinit var items: ArrayList<BirthdayNote>
    private lateinit var cursor: Cursor

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            dbHelper = MyDatabase(context)
            items = ArrayList()

            //Загрузка данных из БД
            cursor = dbHelper.readData()
            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MyDatabase.ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(MyDatabase.NAME))
                val date_day = cursor.getString(cursor.getColumnIndexOrThrow(MyDatabase.BIRTHDAY_DATE_DAY))
                val date_month = cursor.getString(cursor.getColumnIndexOrThrow(MyDatabase.BIRTHDAY_DATE_MONTH))
                val date_year: String? = cursor.getString(cursor.getColumnIndexOrThrow(MyDatabase.BIRTHDAY_DATE_YEAR))

                items.add(BirthdayNote(id, name, date_day, date_month, date_year))
            }

            //Пересоздание уведомлений
            items.forEach { item ->
                setAlarm(context, item.date_day.toInt(), item.date_month.toInt(), item.id.toInt(), item.name)
            }
        }
    }
}