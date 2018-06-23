package com.kennyc.sleeptimer.timer

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class TimerViewModelTest {
    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var timeUpdateObserver: Observer<TimeUpdate>

    @Mock
    private lateinit var timerActiveObserver: Observer<Boolean>

    private lateinit var viewModel: TimerViewModel

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val pref = Mockito.mock(SharedPreferences::class.java, Mockito.RETURNS_DEEP_STUBS)
        Mockito.`when`(pref.getInt(TimerViewModel.KEY_LAST_SELECTED_TIME, TimerViewModel.DEFAULT_TIME))
                .thenReturn(90)

        viewModel = TimerViewModel(pref)
    }

    @Test
    fun testLastSelectedTime() {
        viewModel.time.observeForever(timeUpdateObserver)
        viewModel.getLastTimerSelected()
        Mockito.verify(timeUpdateObserver).onChanged(TimeUpdate(90, true))
    }

    @Test
    fun testTimerActive() {
        viewModel.isTimerActive.observeForever(timerActiveObserver)
        viewModel.setTimerActive(true)
        Mockito.verify(timerActiveObserver).onChanged(true)
        viewModel.setTimerActive(false)
        Mockito.verify(timerActiveObserver).onChanged(false)

    }
}