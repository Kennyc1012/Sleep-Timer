package com.kennyc.sleeptimer.options

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OptionsViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {

    companion object {
        const val TAG = "OptionsViewModel"
        const val KEY_SCREEN_OFF = "$TAG.EXTRA.SCREEN_OFF"
        const val KEY_WIFI_OFF = "$TAG.EXTRA.WIFI_OFF"
        const val KEY_BLUETOOTH_OFF = "$TAG.EXTRA.BLUETOOTH_OFF"
        const val KEY_AUDIO_OFF = "$TAG.EXTRA.AUDIO_OFF"
    }

    val screenOff = MutableLiveData<Boolean>()
    val wiFiOff = MutableLiveData<Boolean>()
    val bluetoothOff = MutableLiveData<Boolean>()
    val audioOff = MutableLiveData<Boolean>()

    init {
        screenOff.value = sharedPreferences.getBoolean(KEY_SCREEN_OFF, true)
        audioOff.value = sharedPreferences.getBoolean(KEY_AUDIO_OFF, true)
        wiFiOff.value = sharedPreferences.getBoolean(KEY_WIFI_OFF, false)
        bluetoothOff.value = sharedPreferences.getBoolean(KEY_BLUETOOTH_OFF, false)
    }

    fun setOptionValue(key: String, value: Boolean) {
        when (key) {
            KEY_SCREEN_OFF -> screenOff.value = value
            KEY_WIFI_OFF -> wiFiOff.value = value
            KEY_BLUETOOTH_OFF -> bluetoothOff.value = value
            KEY_AUDIO_OFF -> audioOff.value = value
        }

        sharedPreferences.edit { putBoolean(key, value) }
    }
}