package com.kennyc.sleeptimer.timer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.core.content.edit

class TimerViewModel(app: Application) : AndroidViewModel(app) {
    companion object {
        const val KEY_LAST_SELECTED_TIME = "TimerViewModel.LAST_SELECTED_TIME"
        const val DEFAULT_TIME = 60
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
        val lastTime = sharedPreferences.getInt(KEY_LAST_SELECTED_TIME, DEFAULT_TIME)
        time.value = Pair(lastTime, true)
    }
}