package com.example.birthdaytrackerv2.ui.calendarview

import android.content.Context
import android.database.Cursor
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.example.birthdaytrackerv2.ui.calendar.CalendarFragment
import com.example.birthdaytrackerv2.ui.data.BirthdayNote
import com.example.birthdaytrackerv2.ui.data.MyDatabase
import java.util.Calendar
import kotlin.math.hypot

class NewCustomCalendarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    //private lateinit var itemsForCurrentMonth: List<ArrayList<BirthdayNote>>
    //private lateinit var itemsFromDbCount: MutableList<Int>
    private lateinit var items: ArrayList<BirthdayNote>
    private lateinit var itemsWithBirthdayCoordinates: List<MutableList<Float>>
    private lateinit var itemsWithBirthdayName: List<ArrayList<String>>
    private lateinit var dbHelper: MyDatabase
    private lateinit var cursor: Cursor
    private var maxDayCount: Int = 0

    private val usualDate = Paint().apply {
        setColor(Color.YELLOW)
    }
    private val birthdayDate = Paint().apply {
        setColor(Color.RED)
    }
    private val currentDate = Paint().apply {
        setColor(Color.RED)
        style = Paint.Style.STROKE
        strokeWidth = 8f
        isAntiAlias = true
    }
    private val textDesign = Paint().apply {
        textSize = 60f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    private val weekDayList = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
    private var calendar = Calendar.getInstance()
    private val currentYear = calendar.get(Calendar.YEAR)
    private val currentMonth = calendar.get(Calendar.MONTH)
    private val currentDayOfWeek = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7
    private val currentWeekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH)

    init {
        setItemsFromDb()
    }

    //TODO Добавить свайп календаря влево или вправо (сложн) (onTouch?)
    //TODO Добавить анимацию перелистывания календаря (сложн)
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val radius = width * 1f / 8 / 2
        val centerX = width * 1f / 7 / 2
        val centerY = height * 1f / 7

        birthdayDate.setColor(Color.GREEN)
        currentDate.setColor(Color.RED)

        maxDayCount = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7

        val textHeight = textDesign.descent() + textDesign.ascent()
        val textOffset = (textHeight / 2f)

        //Дни недели
        for (i in 0..6) {
            canvas.drawText(
                weekDayList[i],
                centerX + i * centerX * 2,
                centerY / 2,
                textDesign
            )
        }

        var currentI = 0
        var position = firstDayOfWeek

        val newItems: MutableSet<Int> = mutableSetOf()
        itemsWithBirthdayName = List(maxDayCount) { ArrayList() }

        //TODO Сделать переходный массив чтобы меньше нагружать машинку в лучших случаях
        //Заполнение множества с днями рождения
        for (i in 0..<items.size) {
            if (Integer.parseInt(items[i].date_month) == calendar.get(Calendar.MONTH) + 1) {
                newItems.add(Integer.parseInt(items[i].date_day))
                itemsWithBirthdayName[Integer.parseInt(items[i].date_day) - 1].add(items[i].name)
            }
        }

        itemsWithBirthdayCoordinates = List(maxDayCount) { mutableListOf(0f, 0f, 0f) }

        for (el in newItems) {
            itemsWithBirthdayCoordinates[el - 1][0] = centerX + ((firstDayOfWeek + el - 1) % 7) * centerX * 2
            itemsWithBirthdayCoordinates[el - 1][1] = centerY + ((firstDayOfWeek + el - 1) / 7) * centerX * 2
            itemsWithBirthdayCoordinates[el - 1][2] = radius
        }

        //Просто рисуем дни
        while (currentI < maxDayCount) {
            canvas.drawCircle(
                centerX + (position % 7) * centerX * 2,
                centerY + (position / 7) * centerX * 2,
                radius,
                usualDate
            )
            currentI++
            canvas.drawText(
                currentI.toString(),
                centerX + (position % 7) * centerX * 2,
                centerY + (position / 7) * centerX * 2 - textOffset,
                textDesign
            )
            position++
        }

        //Рисуем дни рождения
        for (el in newItems) {
            canvas.drawCircle(
                centerX + ((firstDayOfWeek + el - 1) % 7) * centerX * 2,
                centerY + ((firstDayOfWeek + el - 1) / 7) * centerX * 2,
                radius,
                birthdayDate
            )

            canvas.drawText(
                el.toString(),
                centerX + ((firstDayOfWeek + el - 1) % 7) * centerX * 2,
                centerY + ((firstDayOfWeek + el - 1) / 7) * centerX * 2 - textOffset,
                textDesign
            )
        }

        //Рисуем сегоодняжний день
        if (calendar.get(Calendar.YEAR) == currentYear
            && calendar.get(Calendar.MONTH) == currentMonth) {
            canvas.drawCircle(
                centerX + currentDayOfWeek * centerX * 2,
                centerY + currentWeekOfMonth * centerX * 2,
                radius,
                currentDate
            )
        }
    }

    private fun updateMonthDraw(monthNumber: Int) {
        if (monthNumber - calendar.get(Calendar.MONTH) > 1) {
            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1)
        } else if (calendar.get(Calendar.MONTH) - monthNumber > 1) {
            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1)
        }
        calendar.set(Calendar.MONTH, monthNumber)
        //updateSelectedMonthFromDb()
        invalidate()
    }

    fun changeMonth(change: Int) {
        if (change == -1) {
            updateMonthDraw(calendar.get(Calendar.MONTH) - 1)
            return
        }

        updateMonthDraw(calendar.get(Calendar.MONTH) + 1)
    }

    /**
    private fun updateSelectedMonthFromDb() {
        itemsForCurrentMonth = List(maxDayCount) { ArrayList() }
        itemsFromDbCount = MutableList(maxDayCount) {0}

        for (i in 0..<items.size) {
            if (Integer.parseInt(items[i].date_month) == calendar.get(Calendar.MONTH) + 1) {
                itemsForCurrentMonth[Integer.parseInt(items[i].date_day) - 1].add(items[i])
                itemsFromDbCount[Integer.parseInt(items[i].date_day) - 1] += 1
            }
        }
    }
    */

    /**
    //TODO Реализовать логику заполнения массивов itemsForCurrentMonth и itemsFromDbCount
    fun setItemsFromDb(items: ArrayList<BirthdayNote>) {
        this.items = items
        Log.d("TAG", items.toString())
    }
    */

    //TODO В идеале получать items откуда-нибудь, а не загружать их самостоятельно каждый раз
    private fun setItemsFromDb() {
        dbHelper = MyDatabase(context)
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

        cursor.close()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val x = event.x
            val y = event.y
            val birthdayDates: ArrayList<String> = isInsideElement(x, y)

            if (birthdayDates.size != 0) {
                val stringBuilder = StringBuilder()

                if (birthdayDates.size > 2) {
                    stringBuilder.append(birthdayDates[0])
                    stringBuilder.append(": ")
                    stringBuilder.append(birthdayDates[1])
                    stringBuilder.append(" и ещё ${birthdayDates.size - 2}")
                    Toast.makeText(context, stringBuilder.toString(), Toast.LENGTH_SHORT).show()
                    return true
                }

                stringBuilder.append(birthdayDates[0])
                stringBuilder.append(": ")
                stringBuilder.append(birthdayDates[1])
                stringBuilder.append("!")
                Toast.makeText(context, stringBuilder.toString(), Toast.LENGTH_SHORT).show()

                return true
            }
        }

        return super.onTouchEvent(event)
    }

    private fun isInsideElement(x: Float, y: Float): ArrayList<String> {
        var distance: Double
        val birthdayDates: ArrayList<String> = ArrayList()
        var el: MutableList<Float>
        val stringBuilder: StringBuilder = StringBuilder()

        for (i in itemsWithBirthdayCoordinates.indices) {
            el = itemsWithBirthdayCoordinates[i]

            if (el[2] != 0f) {
                distance = hypot((x - el[0]).toDouble(), (y - el[1]).toDouble())

                if (distance < el[2]) {
                    stringBuilder.append(i + 1)
                    stringBuilder.append(".")
                    stringBuilder.append(calendar.get(Calendar.MONTH) + 1)
                    birthdayDates.add(stringBuilder.toString())
                    for (name in itemsWithBirthdayName[i]) {
                        birthdayDates.add(name)
                    }

                    return birthdayDates
                }
            }
        }
        return birthdayDates
    }

    fun getYear() : Int {
        return calendar.get(Calendar.YEAR)
    }

    fun getMonth() : Int {
        return calendar.get(Calendar.MONTH)
    }
}