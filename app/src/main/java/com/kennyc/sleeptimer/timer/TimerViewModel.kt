package com.kennyc.sleeptimer.timer

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TimerViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {
    companion object {
        const val KEY_LAST_SELECTED_TIME = "TimerViewModel.LAST_SELECTED_TIME"
        const val DEFAULT_TIME = 60
    }

    val time = MutableLiveData<TimeUpdate>()

    val isTimerActive = MutableLiveData<Boolean>()

    fun setTime(currentTime: Int) {
        time.value = TimeUpdate(currentTime, false)
    }

    fun setTimerActive(active: Boolean) {
        isTimerActive.value = active
    }

    fun saveLastTimerSettings(time: Int) {
        sharedPreferences.edit { putInt(KEY_LAST_SELECTED_TIME, time) }
    }

    fun getLastTimerSelected() {
        val lastTime = sharedPreferences.getInt(KEY_LAST_SELECTED_TIME, DEFAULT_TIME)
        time.value = TimeUpdate(lastTime, true)
    }
}