package com.kennyc.sleeptimer.options

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.kennyc.sleeptimer.R
import kotlinx.android.synthetic.main.fragment_options.*

class OptionsFragment : Fragment() {
    private lateinit var viewModel: OptionsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(OptionsViewModel::class.java)
        viewModel.screenOff.observe(this, Observer { value -> value?.let { onScreenOffToggle(it) } })
        viewModel.wiFiOff.observe(this, Observer { value -> value?.let { onWiFiToggle(it) } })
        viewModel.bluetoothOff.observe(this, Observer { value -> value?.let { onBluetoothToggle(it) } })
        viewModel.audioOff.observe(this, Observer { value -> value?.let { onAudioToggled(it) } })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_options, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        optionsHomeCB.setOnCheckedChangeListener { _, isChecked -> viewModel.setOptionValue(OptionsViewModel.KEY_SCREEN_OFF, isChecked) }
        optionsWiFiCB.setOnCheckedChangeListener { _, isChecked -> viewModel.setOptionValue(OptionsViewModel.KEY_WIFI_OFF, isChecked) }
        optionsBluetoothCB.setOnCheckedChangeListener { _, isChecked -> viewModel.setOptionValue(OptionsViewModel.KEY_BLUETOOTH_OFF, isChecked) }
        optionsAudioCB.setOnCheckedChangeListener { _, isChecked -> viewModel.setOptionValue(OptionsViewModel.KEY_AUDIO_OFF, isChecked) }
    }

    private fun onScreenOffToggle(isOn: Boolean) {
        optionsHomeCB.isChecked = isOn
    }

    private fun onWiFiToggle(isOn: Boolean) {
        optionsWiFiCB.isChecked = isOn
    }

    private fun onBluetoothToggle(isOn: Boolean) {
        optionsBluetoothCB.isChecked = isOn
    }

    private fun onAudioToggled(isOn: Boolean) {
        optionsAudioCB.isChecked = isOn
    }
}