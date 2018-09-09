package com.kennyc.sleeptimer.main

import android.os.Bundle
import android.preference.PreferenceManager
import android.text.format.DateUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.tabs.TabLayout
import com.kennyc.sleeptimer.R
import com.kennyc.sleeptimer.SleepTimerViewModelFactory
import com.kennyc.sleeptimer.TimerService
import com.kennyc.sleeptimer.options.OptionsFragment
import com.kennyc.sleeptimer.timer.TimerFragment
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
        const val ACTION_START_TIME = "$TAG.ACTION.START_TIMER"
        const val TAB_POSITION_TIMER = 0
        const val TAB_POSITION_OPTIONS = 1
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = SleepTimerViewModelFactory(PreferenceManager.getDefaultSharedPreferences(applicationContext))
        viewModel = ViewModelProviders.of(this, factory).get(MainViewModel::class.java)
        viewModel.currentTabPosition.observe(this, Observer { tab -> tab?.let { onTabChanged(it) } })
        viewModel.fromAppShortcut.observe(this, Observer { result -> result?.let { onAppShortcut(it) } })
        viewModel.checkForAppShortcut(intent)
    }

    private fun onTabChanged(position: Int) {
        Timber.v("onTabChanged: $position")

        when (position) {
            TAB_POSITION_TIMER -> {
                supportFragmentManager.beginTransaction()
                        .replace(R.id.mainContent, TimerFragment())
                        .commit()
            }

            TAB_POSITION_OPTIONS -> {
                supportFragmentManager.beginTransaction()
                        .replace(R.id.mainContent, OptionsFragment())
                        .commit()
            }

            else -> throw IllegalArgumentException("Unable to handle menu item with id $position")
        }
    }

    private fun onAppShortcut(fromShortcut: Boolean) {
        Timber.v("onAppShortcut: $fromShortcut")

        when (fromShortcut) {
            true -> {
                val lastTime = viewModel.getLastSavedTimer()
                val duration = lastTime * DateUtils.MINUTE_IN_MILLIS
                startService(TimerService.createIntent(applicationContext, duration))
                finish()
            }

            else -> {
                setContentView(R.layout.activity_main)
                mainTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    override fun onTabReselected(tab: TabLayout.Tab) {
                        // Ignore
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab) {
                        // Ignore
                    }

                    override fun onTabSelected(tab: TabLayout.Tab) {
                        viewModel.onTabSelected(tab.position)
                    }
                })

                if (viewModel.currentTabPosition.value == null) {
                    viewModel.currentTabPosition.value = TAB_POSITION_TIMER
                }
            }
        }
    }
}