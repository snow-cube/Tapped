package com.example.nfc_task

import android.app.ForegroundServiceStartNotAllowedException
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat

class TaskService : Service() {
    private val binder = TaskBinder()  // Binder对象，用于提供服务的控制接口
    private val handler = Handler()
    private var counter = 0
    private var isRunning = false

    // 定时执行的任务
    private val runnable: Runnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                counter++
                Log.d("TaskService", "Timer count: $counter")

                // 再次安排任务，每1秒执行一次
                handler.postDelayed(this, 1000)
            }
        }
    }

    // Binder类，提供给Activity进行交互
    inner class TaskBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods.
        fun getService(): TaskService = this@TaskService


    }

    private fun startForeground() {
        // Before starting the service as foreground check that the app has the
        // appropriate runtime permissions.
//        val cameraPermission =
//            PermissionChecker.checkSelfPermission(this, Manifest.permission.CAMERA)
//        if (cameraPermission != PermissionChecker.PERMISSION_GRANTED) {
//            // Without camera permissions the service cannot run in the foreground
//            // Consider informing user or updating your app UI if visible.
//            stopSelf()
//            return
//        }

        try {
            val notification = NotificationCompat.Builder(this, "CHANNEL_ID")
                // Create the notification to display while the service is running
                .build()
            ServiceCompat.startForeground(
                /* service = */ this,
                /* id = */ 100, // Cannot be 0
                /* notification = */ notification,
                /* foregroundServiceType = */
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
                } else {
                    0
                }
            )
        } catch (
            e: Exception
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                && e is ForegroundServiceStartNotAllowedException
            ) {
                // App not in a valid state to start foreground service
                // (e.g. started from bg)
            }
            // ...
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("TaskService", "Service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground()

        Log.d("TaskService", "Service started")
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
        Log.d("TaskService", "Service destroyed")
    }

    // 绑定时返回 Binder 对象
    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    fun startTask() {
        this.startTimer()
    }

    fun pauseTask() {
        this.pauseTimer()
    }

    fun finishTask() {
        this.stopTimer()
    }

    fun stopTask() {
        this.stopTimer()
    }

    fun getCurrentTaskTime(): Int {
        return getCurrentTime()
    }

    // 开启计时器
    private fun startTimer() {
        if (!isRunning) {
            isRunning = true
            handler.post(runnable)
            Log.d("TimerService", "Timer started")
        }
    }

    // 暂停计时器
    private fun pauseTimer() {
        isRunning = false
        Log.d("TimerService", "Timer paused")
    }

    // 停止计时器，并重置计时
    private fun stopTimer() {
        isRunning = false
        handler.removeCallbacks(runnable)
        counter = 0
        Log.d("TimerService", "Timer stopped and reset")
    }

    // 获取当前计时器的时间
    fun getCurrentTime(): Int {
        return counter
    }
}