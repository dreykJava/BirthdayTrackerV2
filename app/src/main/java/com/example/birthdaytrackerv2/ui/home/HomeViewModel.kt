package com.example.birthdaytrackerv2.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Ближайший день рождения: "
    }
    val text: LiveData<String> = _text

    private val _name = MutableLiveData<String>().apply {
        value = "Максим Громов"
    }
    val name: LiveData<String> = _name
}