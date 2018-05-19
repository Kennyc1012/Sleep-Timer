package com.kennyc.sleeptimer.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.kennyc.sleeptimer.R
import com.kennyc.sleeptimer.options.OptionsFragment
import com.kennyc.sleeptimer.timer.TimerFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel.currentTab.observe(this, Observer { tab -> onTabChanged(tab) })
        mainTabs.setOnNavigationItemSelectedListener { tab ->
            viewModel.onTabSelected(tab)
            true
        }

        if (viewModel.currentTab.value == null) mainTabs.selectedItemId = R.id.tabTimer
        supportActionBar?.let { it.title = getString(R.string.tab_timer) }
    }

    private fun onTabChanged(menuItem: MenuItem?) {
        menuItem?.let {
            when (it.itemId) {
                R.id.tabTimer -> {
                    supportActionBar?.let { it.title = getString(R.string.tab_timer) }
                    supportFragmentManager.beginTransaction()
                            .replace(R.id.mainContent, TimerFragment())
                            .commit()
                }

                R.id.tabOptions -> {
                    supportActionBar?.let { it.title = getString(R.string.tab_options) }
                    supportFragmentManager.beginTransaction()
                            .replace(R.id.mainContent, OptionsFragment())
                            .commit()
                }

                else -> throw IllegalArgumentException("Unable to handle menu item with id " + it.itemId)
            }
        }

    }
}