package com.kennyc.sleeptimer.main

import android.os.Bundle
import android.preference.PreferenceManager
import android.text.format.DateUtils
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.kennyc.sleeptimer.R
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
        val factory = MainViewModelFactory(PreferenceManager.getDefaultSharedPreferences(applicationContext))
        viewModel = ViewModelProviders.of(this, factory).get(MainViewModel::class.java)
        viewModel.currentTab.observe(this, Observer { tab -> onTabChanged(tab) })
        viewModel.fromAppShortcut.observe(this, Observer { result -> onAppShortcut(result) })
        viewModel.checkForAppShortcut(intent)
    }

    private fun onTabChanged(menuItem: MenuItem?) {
        menuItem?.let {
            Timber.v("onTabChanged: $menuItem")

            when (it.itemId) {
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

                else -> throw IllegalArgumentException("Unable to handle menu item with id " + it.itemId)
            }
        }

    }

    private fun onAppShortcut(fromShortcut: Boolean?) {
        fromShortcut?.let {
            Timber.v("onAppShortcut: $it")

            when (it) {
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
}