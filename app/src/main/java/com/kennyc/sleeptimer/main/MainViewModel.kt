package com.kennyc.sleeptimer.main

import android.content.Intent
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kennyc.sleeptimer.timer.TimerViewModel

class MainViewModel(private val pref: SharedPreferences) : ViewModel() {

    val currentTabPosition = MutableLiveData<Int>()
    val fromAppShortcut = MutableLiveData<Boolean>()

    fun onTabSelected(position: Int) {
        val currentPosition = currentTabPosition.value
        if (currentPosition == null || currentPosition != position) currentTabPosition.value = position
    }

    fun checkForAppShortcut(intent: Intent?) {
        fromAppShortcut.value = MainActivity.ACTION_START_TIME == intent?.action
    }

    fun getLastSavedTimer(): Int {
        return pref.getInt(TimerViewModel.KEY_LAST_SELECTED_TIME, TimerViewModel.DEFAULT_TIME)
    }
}