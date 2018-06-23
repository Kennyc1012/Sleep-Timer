package com.kennyc.sleeptimer.timer

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.preference.PreferenceManager
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.kennyc.sleeptimer.R
import com.kennyc.sleeptimer.SleepTimerViewModelFactory
import com.kennyc.sleeptimer.TimerService
import kotlinx.android.synthetic.main.fragment_timer.*
import timber.log.Timber


class TimerFragment : Fragment() {

    private lateinit var viewModel: TimerViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_timer, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val factory = SleepTimerViewModelFactory(PreferenceManager.getDefaultSharedPreferences(activity))
        viewModel = ViewModelProviders.of(this, factory).get(TimerViewModel::class.java)
        viewModel.isTimerActive.observe(this, Observer { active -> active?.let { onTimerActive(it) } })
        viewModel.time.observe(this, Observer { time -> time?.let { onTimeChange(it) } })
        timerSeekBar.setOnValueChangedListener { time -> viewModel.setTime(time) }

        timerFab.setOnClickListener {
            val active = viewModel.isTimerActive.value ?: false
            viewModel.setTimerActive(!active)
        }

        viewModel.getLastTimerSelected()
        context?.let {
            it.bindService(TimerService.createIntent(it, 0), connection, 0)
        }
    }

    private fun onTimeChange(result: TimeUpdate) {
        val time = result.time
        val updateSeekBar = result.updateSeekBar

        when {
            time < 1 -> timerTime.text = getString(R.string.timer_less_than_minute)

            time >= 1 -> timerTime.text = resources.getQuantityString(R.plurals.timer_minutes, time, time)
        }

        if (updateSeekBar) timerSeekBar.value = time
    }

    private fun onTimerActive(active: Boolean) {
        when (active) {
            true -> {
                viewModel.saveLastTimerSettings(timerSeekBar.value)
                timerFab.setImageResource(R.drawable.ic_timer_off_white_24dp)
                val duration = timerSeekBar.value * DateUtils.MINUTE_IN_MILLIS
                context?.let { it.startService(TimerService.createIntent(it, duration)) }
            }

            false -> {
                timerFab.setImageResource(R.drawable.ic_done_white_24dp)
                context?.let { it.stopService(TimerService.createIntent(it, 0)) }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        context?.registerReceiver(receiver, IntentFilter(TimerService.ACTION_BROADCAST_TIMER_END))
    }

    override fun onPause() {
        super.onPause()
        context?.unregisterReceiver(receiver)
    }

    override fun onDestroy() {
        super.onDestroy()

        try {
            context?.unbindService(connection)
        } catch (ex: Exception) {
            // Ignore
        }
    }

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            Timber.v("onServiceConnected")
            viewModel.setTimerActive(true)
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            Timber.v("onServiceDisconnected")
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (TimerService.ACTION_BROADCAST_TIMER_END == intent?.action) {
                Timber.v("onReceive")
                viewModel.setTimerActive(false)
            }
        }
    }
}