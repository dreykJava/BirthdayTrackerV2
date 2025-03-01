package com.example.birthdaytrackerv2.ui.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private val _sharedData = MutableLiveData<ArrayList<BirthdayNote>>()
    val sharedData: LiveData<ArrayList<BirthdayNote>> get() = _sharedData

    fun setData(data: ArrayList<BirthdayNote>) {
        _sharedData.value = data
    }
}