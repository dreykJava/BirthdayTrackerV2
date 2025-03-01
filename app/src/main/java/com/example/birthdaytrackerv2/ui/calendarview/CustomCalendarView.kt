package com.example.birthdaytrackerv2.ui.calendarview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.example.birthdaytrackerv2.ui.data.BirthdayNote
import java.util.Calendar

class CustomCalendarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        color = Color.BLACK
        textSize = 40f
        textAlign = Paint.Align.CENTER
    }

    private val days = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
    private var currentMonth = Calendar.getInstance()
    private var dates = mutableListOf<String>()
    private var selectedDate: String? = null
    private var eventDates = mutableListOf<String>() // Даты из базы данных

    init {
        updateDatesForCurrentMonth()
    }

    private fun updateDatesForCurrentMonth() {
        dates.clear()
        val calendar = currentMonth.clone() as Calendar
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        // Добавляем пустые строки для дней, которые не относятся к текущему месяцу
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        for (i in 1 until firstDayOfWeek) {
            dates.add("")
        }

        // Добавляем дни месяца
        val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (i in 1..maxDay) {
            dates.add(i.toString())
        }

        invalidate() // Перерисовываем View
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val cellWidth = width / 7f
        val cellHeight = height / 6f

        // Рисуем дни недели
        days.forEachIndexed { index, day ->
            canvas.drawText(day, cellWidth * index + cellWidth / 2, cellHeight / 2, paint)
        }

        // Рисуем даты
        dates.forEachIndexed { index, date ->
            val row = index / 7
            val col = index % 7
            val x = cellWidth * col + cellWidth / 2
            val y = cellHeight * (row + 1) + cellHeight / 2

            // Подсвечиваем даты из базы данных
            if (eventDates.contains("${currentMonth.get(Calendar.YEAR)}-${currentMonth.get(Calendar.MONTH) + 1}-$date")) {
                paint.color = Color.RED
            } else {
                paint.color = Color.BLACK
            }

            canvas.drawText(date, x, y, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val x = event.x
                val y = event.y

                val cellWidth = width / 7f
                val cellHeight = height / 6f

                val col = (x / cellWidth).toInt()
                val row = (y / cellHeight).toInt() - 1 // -1, чтобы пропустить строку с днями недели

                if (row >= 0 && col >= 0) {
                    val selectedDate = dates.getOrNull(row * 7 + col)
                    selectedDate?.let {
                        val fullDate = "${currentMonth.get(Calendar.YEAR)}-${currentMonth.get(Calendar.MONTH) + 1}-$it"
                        if (eventDates.contains(fullDate)) {
                            // Показываем сообщение с выбранной датой
                            showDateMessage(fullDate)
                        }
                    }
                }
            }
        }
        return true
    }

    private fun showDateMessage(date: String) {
        // Показываем Toast с выбранной датой
        Toast.makeText(context, "Выбрана дата: $date", Toast.LENGTH_SHORT).show()
    }

    fun setCurrentMonth(calendar: Calendar) {
        currentMonth = calendar
        updateDatesForCurrentMonth()
    }

    fun setEventDates(items: ArrayList<BirthdayNote>) {
        eventDates.clear()

        for (birthNote in items) {
            eventDates.add("${birthNote.date_day}.${birthNote.date_month}")
        }

        invalidate() // Перерисовываем View
    }
}