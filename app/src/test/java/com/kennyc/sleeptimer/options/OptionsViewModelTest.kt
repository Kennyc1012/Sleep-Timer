package com.kennyc.sleeptimer.options

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class OptionsViewModelTest {
    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private lateinit var viewModel: OptionsViewModel

    @Mock
    private lateinit var observer: Observer<Boolean>

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val pref = Mockito.mock(SharedPreferences::class.java, Mockito.RETURNS_DEEP_STUBS)
        Mockito.`when`(pref.getBoolean(OptionsViewModel.KEY_SCREEN_OFF, true)).thenReturn(true)
        Mockito.`when`(pref.getBoolean(OptionsViewModel.KEY_AUDIO_OFF, true)).thenReturn(true)
        Mockito.`when`(pref.getBoolean(OptionsViewModel.KEY_BLUETOOTH_OFF, false)).thenReturn(true)
        Mockito.`when`(pref.getBoolean(OptionsViewModel.KEY_WIFI_OFF, false)).thenReturn(true)
        viewModel = OptionsViewModel(pref)
    }

    @Test
    fun testSavedValues() {
        Assert.assertTrue(viewModel.screenOff.value!!)
        Assert.assertTrue(viewModel.wiFiOff.value!!)
        Assert.assertTrue(viewModel.bluetoothOff.value!!)
        Assert.assertTrue(viewModel.audioOff.value!!)
    }

    @Test
    fun testScreenOff() {
        viewModel.screenOff.observeForever(observer)
        viewModel.setOptionValue(OptionsViewModel.KEY_SCREEN_OFF, true)
        Mockito.verify(observer, Mockito.atLeast(2)).onChanged(true)
        viewModel.setOptionValue(OptionsViewModel.KEY_SCREEN_OFF, false)
        Mockito.verify(observer).onChanged(false)
    }

    @Test
    fun testAudio() {
        viewModel.audioOff.observeForever(observer)
        viewModel.setOptionValue(OptionsViewModel.KEY_AUDIO_OFF, true)
        Mockito.verify(observer, Mockito.atLeast(2)).onChanged(true)
        viewModel.setOptionValue(OptionsViewModel.KEY_AUDIO_OFF, false)
        Mockito.verify(observer).onChanged(false)
    }

    @Test
    fun testWifi() {
        viewModel.wiFiOff.observeForever(observer)
        viewModel.setOptionValue(OptionsViewModel.KEY_WIFI_OFF, true)
        Mockito.verify(observer, Mockito.atLeast(2)).onChanged(true)
        viewModel.setOptionValue(OptionsViewModel.KEY_WIFI_OFF, false)
        Mockito.verify(observer).onChanged(false)
    }

    @Test
    fun tesBluetooth() {
        viewModel.bluetoothOff.observeForever(observer)
        viewModel.setOptionValue(OptionsViewModel.KEY_BLUETOOTH_OFF, true)
        Mockito.verify(observer, Mockito.atLeast(2)).onChanged(true)
        viewModel.setOptionValue(OptionsViewModel.KEY_BLUETOOTH_OFF, false)
        Mockito.verify(observer).onChanged(false)
    }
}