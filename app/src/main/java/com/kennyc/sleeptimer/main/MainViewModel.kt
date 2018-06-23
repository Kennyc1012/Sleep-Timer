package com.kennyc.sleeptimer.main

import android.content.Intent
import android.content.SharedPreferences
import android.view.MenuItem
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kennyc.sleeptimer.timer.TimerViewModel

class MainViewModel(private val pref: SharedPreferences) : ViewModel() {

    val currentTab = MutableLiveData<MenuItem>()
    val fromAppShortcut = MutableLiveData<Boolean>()

    fun onTabSelected(menuItem: MenuItem) {
        val currentMenuItem = currentTab.value
        if (currentMenuItem == null || currentMenuItem != menuItem) currentTab.value = menuItem
    }

    fun checkForAppShortcut(intent: Intent?) {
        fromAppShortcut.value = MainActivity.ACTION_START_TIME == intent?.action
    }

    fun getLastSavedTimer(): Int {
        return pref.getInt(TimerViewModel.KEY_LAST_SELECTED_TIME, TimerViewModel.DEFAULT_TIME)
    }
}