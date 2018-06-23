package com.kennyc.sleeptimer.main

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainViewModelFactory(private val pref: SharedPreferences) : ViewModelProvider.NewInstanceFactory() {


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass == MainViewModel::class.java) {
            return MainViewModel(pref) as (T)
        }

        throw IllegalArgumentException("Unable to handle class ${modelClass.canonicalName}")
    }
}