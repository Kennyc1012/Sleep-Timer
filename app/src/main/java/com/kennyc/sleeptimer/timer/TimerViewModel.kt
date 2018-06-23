package com.kennyc.sleeptimer.timer

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class TimerViewModel(app: Application) : AndroidViewModel(app) {
    companion object {
        const val KEY_LAST_SELECTED_TIME = "TimerViewModel.LAST_SELECTED_TIME"
        const val DEFAULT_TIME = 60
    }

    private val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(app)

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