package com.kennyc.sleeptimer.timer

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.core.content.edit

class TimerViewModel(app: Application) : AndroidViewModel(app) {
    companion object {
        const val KEY_LAST_SELECTED_TIME = "TimerViewModel.LAST_SELECTED_TIME"
    }

    private val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(app)

    val time = MutableLiveData<Pair<Int, Boolean>>()

    val isTimerActive = MutableLiveData<Boolean>()

    fun setTime(currentTime: Int) {
        time.value = Pair(currentTime, false)
    }

    fun setTimerActive(active: Boolean) {
        isTimerActive.value = active
    }

    fun saveLastTimerSettings(time: Int) {
        sharedPreferences.edit { putInt(KEY_LAST_SELECTED_TIME, time) }
    }

    fun getLastTimerSelected() {
        val lastTime = sharedPreferences.getInt(KEY_LAST_SELECTED_TIME, 60)
        time.value = Pair(lastTime, true)
    }
}