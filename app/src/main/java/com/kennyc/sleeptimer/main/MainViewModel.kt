package com.kennyc.sleeptimer.main

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.Intent
import android.os.Build
import android.preference.PreferenceManager
import android.view.MenuItem
import com.kennyc.sleeptimer.timer.TimerViewModel

class MainViewModel(app: Application) : AndroidViewModel(app) {

    val currentTab = MutableLiveData<MenuItem>()
    val fromAppShortcut = MutableLiveData<Boolean>()

    fun onTabSelected(menuItem: MenuItem) {
        val currentMenuItem = currentTab.value
        if (currentMenuItem == null || currentMenuItem != menuItem) currentTab.value = menuItem
    }

    fun checkForAppShirtcut(intent: Intent?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            intent?.let { fromAppShortcut.value = MainActivity.ACTION_START_TIME == intent.action }
        } else {
            fromAppShortcut.value = false
        }
    }

    fun getLastSavedTimer(): Int {
        return PreferenceManager.getDefaultSharedPreferences(getApplication())
                .getInt(TimerViewModel.KEY_LAST_SELECTED_TIME, TimerViewModel.DEFAULT_TIME)
    }
}