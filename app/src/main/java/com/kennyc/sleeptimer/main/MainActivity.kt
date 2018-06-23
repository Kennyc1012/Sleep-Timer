package com.kennyc.sleeptimer.main

import android.os.Bundle
import android.preference.PreferenceManager
import android.text.format.DateUtils
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
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
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = SleepTimerViewModelFactory(PreferenceManager.getDefaultSharedPreferences(applicationContext))
        viewModel = ViewModelProviders.of(this, factory).get(MainViewModel::class.java)
        viewModel.currentTab.observe(this, Observer { tab -> tab?.let { onTabChanged(it) } })
        viewModel.fromAppShortcut.observe(this, Observer { result -> result?.let { onAppShortcut(it) } })
        viewModel.checkForAppShortcut(intent)
    }

    private fun onTabChanged(menuItem: MenuItem) {
        Timber.v("onTabChanged: $menuItem")

        when (menuItem.itemId) {
            R.id.tabTimer -> {
                supportActionBar?.title = getString(R.string.tab_timer)
                supportFragmentManager.beginTransaction()
                        .replace(R.id.mainContent, TimerFragment())
                        .commit()
            }

            R.id.tabOptions -> {
                supportActionBar?.title = getString(R.string.tab_options)
                supportFragmentManager.beginTransaction()
                        .replace(R.id.mainContent, OptionsFragment())
                        .commit()
            }

            else -> throw IllegalArgumentException("Unable to handle menu item with id " + menuItem.itemId)
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
                mainTabs.setOnNavigationItemSelectedListener { tab ->
                    viewModel.onTabSelected(tab)
                    true
                }

                if (viewModel.currentTab.value == null) mainTabs.selectedItemId = R.id.tabTimer
                supportActionBar?.title = getString(R.string.tab_timer)
            }
        }
    }
}