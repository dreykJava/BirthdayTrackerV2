package com.example.birthdaytrackerv2.ui.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CalendarViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Ближайший день рождения: "
    }
    val text: LiveData<String> = _text
}