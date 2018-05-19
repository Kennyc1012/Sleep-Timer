package com.kennyc.sleeptimer.main

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.view.MenuItem

class MainViewModel : ViewModel() {

    val currentTab: MutableLiveData<MenuItem> = MutableLiveData()

    fun onTabSelected(menuItem: MenuItem) {
        val currentMenuItem = currentTab.value
        if (currentMenuItem == null || currentMenuItem != menuItem) currentTab.value = menuItem
    }
}