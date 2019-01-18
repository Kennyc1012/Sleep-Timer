package com.kennyc.sleeptimer

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.net.wifi.WifiManager
import android.os.Binder
import android.os.CountDownTimer
import android.os.IBinder
import android.preference.PreferenceManager
import android.text.format.DateUtils
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.edit
import com.kennyc.sleeptimer.options.OptionsViewModel
import timber.log.Timber


class TimerService : Service() {

    companion object {
        const val TAG = "TimerService"
        const val ACTION_BROADCAST_NOTIFICATION = "$TAG.EXTRA.BROADCAST_NOTIFICATION"
        const val ACTION_BROADCAST_TIMER_END = "$TAG.EXTRA.BROADCAST_TIMER_END"
        const val EXTRA_DURATION = "$TAG.END_TIME"
        const val EXTRA_ADD_TIME = "$TAG.EXTRA.ADD_TIME"
        const val EXTRA_CANCEL = "$TAG.EXTRA.CANCEL"
        const val NOTIFICATION_ID = 999
        const val CHANNEL_ID = "TimerNotificationId"
        const val PREF_KEY_IS_IN_FOREGROUND = "pref_key_is_in_foreground"

        fun createIntent(context: Context, duration: Long): Intent {
            return Intent(context, TimerService::class.java).putExtra(EXTRA_DURATION, duration)
        }
    }

    private val binder = TimerBinder()
    private lateinit var notificationManager: NotificationManagerCompat
    private var countDownTimer: CountDownTimer? = null
    private var firstTick = true
    private var endTime = 0L

    override fun onCreate() {
        super.onCreate()
        Timber.v("onCreate")
        notificationManager = NotificationManagerCompat.from(applicationContext)
        registerReceiver(receiver, IntentFilter(ACTION_BROADCAST_NOTIFICATION))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.v("onStartCommand")

        var duration = 0L

        if (intent != null && intent.hasExtra(EXTRA_DURATION)) {
            duration = intent.getLongExtra(EXTRA_DURATION, 0)
        } else {
            Timber.w("Did not receive any extras")
            stopSelf()
        }

        if (duration > 0) {
            endTime = System.currentTimeMillis() + duration
            startForeground(NOTIFICATION_ID, createNotification((duration / DateUtils.MINUTE_IN_MILLIS).toInt()))
            startTimer(duration)
        } else {
            stopSelf()
        }

        return Service.START_STICKY
    }

    override fun onDestroy() {
        Timber.v("onDestroy")
        countDownTimer?.cancel()
        unregisterReceiver(receiver)
        PreferenceManager.getDefaultSharedPreferences(applicationContext).edit {
            putBoolean(PREF_KEY_IS_IN_FOREGROUND, false)
        }
        super.onDestroy()
    }

    private fun startTimer(duration: Long) {
        if (countDownTimer != null) return

        countDownTimer = object : CountDownTimer(duration, DateUtils.MINUTE_IN_MILLIS) {
            override fun onFinish() {
                Timber.v("onFinish")
                onSleepTimerEnd()
                stopSelf()
            }

            override fun onTick(millisUntilFinished: Long) {
                Timber.v("onTick, millisUntilFinished: $millisUntilFinished")

                if (!firstTick) {
                    val minutes = (millisUntilFinished / DateUtils.MINUTE_IN_MILLIS).toInt()
                    notificationManager.notify(NOTIFICATION_ID, createNotification(minutes))
                }

                firstTick = false
            }
        }.start()
    }

    private fun createNotification(minutesRemaining: Int): Notification {
        val contentText = resources.getQuantityString(R.plurals.timer_remaining, minutesRemaining, minutesRemaining)

        val cancelIntent = Intent(ACTION_BROADCAST_NOTIFICATION).putExtra(EXTRA_CANCEL, true)
        val cancelPIntent = PendingIntent.getBroadcast(applicationContext, 0, cancelIntent, 0)
        val addIntent = Intent(ACTION_BROADCAST_NOTIFICATION).putExtra(EXTRA_ADD_TIME, true)
        val addPIntent = PendingIntent.getBroadcast(applicationContext, 1, addIntent, 0)

        return NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_access_time_white_24dp)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(contentText)
                .addAction(R.drawable.ic_add_white_24dp, getString(R.string.timer_extend), addPIntent)
                .addAction(R.drawable.ic_timer_off_white_24dp, getString(R.string.timer_end), cancelPIntent)
                .build()
    }

    private fun onSleepTimerEnd() {
        Timber.v("onSleepTimerEnd")
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val screenOff = sharedPref.getBoolean(OptionsViewModel.KEY_SCREEN_OFF, true)
        val wiFiOff = sharedPref.getBoolean(OptionsViewModel.KEY_WIFI_OFF, false)
        val bluetoothOff = sharedPref.getBoolean(OptionsViewModel.KEY_BLUETOOTH_OFF, false)
        val audioOff = sharedPref.getBoolean(OptionsViewModel.KEY_AUDIO_OFF, false)

        if (screenOff) {
            Timber.v("Turning off screen")
            val startMain = Intent(Intent.ACTION_MAIN)
            startMain.addCategory(Intent.CATEGORY_HOME)
            startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(startMain)
        }

        if (wiFiOff) {
            Timber.v("Turning off WiFi")
            val wifiManager = this.getSystemService(Context.WIFI_SERVICE) as WifiManager
            wifiManager.isWifiEnabled = false
        }

        if (bluetoothOff) {
            Timber.v("Turning off bluetooth")
            BluetoothAdapter.getDefaultAdapter()?.let { if (it.isEnabled) it.disable() }
        }

        if (audioOff) {
            Timber.v("Turning off audio")
            val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
        }

        applicationContext.sendBroadcast(Intent(ACTION_BROADCAST_TIMER_END))
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                Timber.v("onReceive")
                val cancel = it.getBooleanExtra(EXTRA_CANCEL, false)
                val addTime = it.getBooleanExtra(EXTRA_ADD_TIME, false)

                when {
                    cancel -> {
                        stopSelf()
                        applicationContext.sendBroadcast(Intent(ACTION_BROADCAST_TIMER_END))
                    }

                    addTime -> {
                        val elapsedTime = endTime - System.currentTimeMillis()
                        val newDuration = elapsedTime + (DateUtils.MINUTE_IN_MILLIS * 10)
                        countDownTimer?.cancel()
                        countDownTimer = null
                        endTime = System.currentTimeMillis() + newDuration
                        startTimer(newDuration)
                    }
                }
            }
        }
    }

    inner class TimerBinder : Binder() {
        fun getService(): TimerService {
            return this@TimerService
        }
    }
}