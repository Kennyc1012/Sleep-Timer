package com.kennyc.sleeptimer.main

import android.content.Intent
import android.content.SharedPreferences
import android.view.MenuItem
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class MainViewModelTest {
    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private lateinit var viewModel: MainViewModel

    @Mock
    private lateinit var intentObserver: Observer<Boolean>

    @Mock
    private lateinit var tabObserver: Observer<MenuItem>

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val pref = Mockito.mock(SharedPreferences::class.java, Mockito.RETURNS_DEEP_STUBS)
        viewModel = MainViewModel(pref)
    }

    @Test
    fun testFromAppShortcutValid() {
        val intent = Mockito.mock(Intent::class.java, Mockito.RETURNS_DEEP_STUBS)
        Mockito.`when`(intent.action).thenReturn(MainActivity.ACTION_START_TIME)
        viewModel.fromAppShortcut.observeForever(intentObserver)
        viewModel.checkForAppShortcut(intent)
        Mockito.verify(intentObserver).onChanged(true)
    }

    @Test
    fun testFromAppShortcutInvalid() {
        val intent = Mockito.mock(Intent::class.java, Mockito.RETURNS_DEEP_STUBS)
        Mockito.`when`(intent.action).thenReturn("something else")
        viewModel.fromAppShortcut.observeForever(intentObserver)
        viewModel.checkForAppShortcut(intent)
        Mockito.verify(intentObserver).onChanged(false)
    }

    @Test
    fun testTabChanged() {
        val menuItem = Mockito.mock(MenuItem::class.java, Mockito.RETURNS_DEEP_STUBS)
        viewModel.currentTab.observeForever(tabObserver)
        viewModel.onTabSelected(menuItem)
        Mockito.verify(tabObserver).onChanged(menuItem)
    }
}
