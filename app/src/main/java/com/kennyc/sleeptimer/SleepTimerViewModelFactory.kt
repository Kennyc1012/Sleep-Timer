package com.kennyc.sleeptimer

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kennyc.sleeptimer.main.MainViewModel
import com.kennyc.sleeptimer.options.OptionsViewModel
import com.kennyc.sleeptimer.timer.TimerViewModel

class SleepTimerViewModelFactory(private val pref: SharedPreferences)
    : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        when (modelClass) {
            MainViewModel::class.java -> return MainViewModel(pref) as (T)

            TimerViewModel::class.java -> return TimerViewModel(pref) as (T)

            OptionsViewModel::class.java -> return OptionsViewModel(pref) as (T)
        }

        throw IllegalArgumentException("Unable to create view model for class ${modelClass.canonicalName}")
    }
}