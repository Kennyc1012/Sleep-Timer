package com.kennyc.sleeptimer.options

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kennyc.sleeptimer.R
import kotlinx.android.synthetic.main.fragment_options.*

class OptionsFragment : Fragment() {
    private lateinit var viewModel: OptionsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(OptionsViewModel::class.java)
        viewModel.screenOff.observe(this, Observer { value -> onScreenOffToggle(value) })
        viewModel.wiFiOff.observe(this, Observer { value -> onWiFiToggle(value) })
        viewModel.bluetoothOff.observe(this, Observer { value -> onBluetoothfToggle(value) })
        viewModel.audioOff.observe(this, Observer { value -> onAudioToggled(value) })
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

    private fun onScreenOffToggle(isOn: Boolean?) {
        isOn?.let {
            optionsHomeCB.isChecked = isOn
        }
    }

    private fun onWiFiToggle(isOn: Boolean?) {
        isOn?.let {
            optionsWiFiCB.isChecked = isOn
        }
    }

    private fun onBluetoothfToggle(isOn: Boolean?) {
        isOn?.let {
            optionsBluetoothCB.isChecked = isOn
        }
    }

    private fun onAudioToggled(isOn: Boolean?) {
        isOn?.let {
            optionsAudioCB.isChecked = isOn
        }
    }
}