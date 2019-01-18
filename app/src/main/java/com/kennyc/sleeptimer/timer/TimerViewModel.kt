package com.kennyc.sleeptimer.timer

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kennyc.sleeptimer.TimerService

class TimerViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {
    companion object {
        const val KEY_LAST_SELECTED_TIME = "TimerViewModel.LAST_SELECTED_TIME"
        const val DEFAULT_TIME = 60
    }

    private val _time = MutableLiveData<TimeUpdate>()
    val time: LiveData<TimeUpdate> = _time

    private val _isTimerActive = MutableLiveData<Boolean>()
    val isTimerActive: LiveData<Boolean> = _isTimerActive

    fun setTime(currentTime: Int) {
        _time.value = TimeUpdate(currentTime, false)
    }

    fun setTimerActive(active: Boolean) {
        _isTimerActive.value = active
        sharedPreferences.edit {
            putBoolean(TimerService.PREF_KEY_IS_IN_FOREGROUND, active)
        }
    }

    fun saveLastTimerSettings(time: Int) {
        sharedPreferences.edit {
            putInt(KEY_LAST_SELECTED_TIME, time)
        }
    }

    fun getLastTimerSelected() {
        val lastTime = sharedPreferences.getInt(KEY_LAST_SELECTED_TIME, DEFAULT_TIME)
        _time.value = TimeUpdate(lastTime, true)
    }

    fun checkIfTimerIsActive() {
        _isTimerActive.value = sharedPreferences.getBoolean(TimerService.PREF_KEY_IS_IN_FOREGROUND, false)
    }
}