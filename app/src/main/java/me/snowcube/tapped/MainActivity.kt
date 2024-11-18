package me.snowcube.tapped

import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import me.snowcube.tapped.data.source.local.Task
import me.snowcube.tapped.models.TappedAppViewModel
import me.snowcube.tapped.models.NfcWritingState
import me.snowcube.tapped.tools.NfcManager
import me.snowcube.tapped.ui.components.TappedApp
import me.snowcube.tapped.ui.theme.TappedTheme

@AndroidEntryPoint()
class MainActivity : ComponentActivity() {

    private val viewModel: TappedAppViewModel by viewModels()

    private var taskService: TaskService? = null
    private var isBound = false

    private val handler = Handler()
    private val runnable: Runnable = object : Runnable {
        override fun run() {
            if (isBound) {
//                val currentCounter = taskService?.getCurrentCounter() ?: 0
                viewModel.updateTaskProcessState(
                    hasTaskProcess = taskService?.hasTaskProcess ?: false,
                    isRunning = taskService?.isRunning ?: false,
                    currentTaskTime = taskService?.getCurrentTaskTime() ?: 0
                )

//                Log.d("msgUpdater", "Update data from service")

                handler.postDelayed(this, 1000)  // 每 1 秒执行一次
            }
        }
    }

    // ServiceConnection 用于监听服务的连接和断开
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as TaskService.TaskBinder
            taskService = binder.getService()  // 获取到 TaskService 的实例
            isBound = true
            handler.post(runnable)  // 开始定期获取数据

            // 绑定服务后，根据服务的 taskId 从数据库获取任务基本信息（如应用重新启动后只能从服务得知任务 ID）
            taskService?.taskId?.let { viewModel.updateRunningTaskInfo(it) }

            if (pendingNfcMessage != null) {
                Log.d("TaskApp.MainActivity", "Handle NFC message when service connected")
                handleNfcMessage()
            }

            Log.d("TaskApp.MainActivity", "Service connected")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            taskService = null
            isBound = false
            handler.removeCallbacks(runnable)  // 确保停止定期获取数据
            Log.d("TaskApp.MainActivity", "Service disconnected")
        }
    }

    private lateinit var writeTagFilters: Array<IntentFilter>
    private var pendingIntent: PendingIntent? = null
    private var nfcManager: NfcManager? = null

    private var pendingNfcMessage: String? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        setContent {
            TappedTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                TappedApp(
                    onStartNewTask = createTaskProcess,
                    finishTaskProcess = finishTaskProcess,
                    completeTask = viewModel::completeTask,
                    onTerminateTask = terminateTaskProcess,
                    onPauseTask = pauseTask,
                    onContinueTask = startOrContinueTask,
                    onWriteClick = {
                        // TODO: 应该先保存生成 ID 后再写入
                        // TODO: UI 部分也应对 NFC 是否可用做出检查
                        if (nfcManager != null && nfcManager!!.nfcAvailable) {
                            nfcManager?.setPendingMsgToWrite("testtaskid")
                            viewModel.setWritingState(NfcWritingState.Writing)
                        } else {
                            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG)
                                .show()
                        }
                    },
                    tappedUiState = uiState,
                    onCloseWritingClick = {
                        nfcManager?.clearPendingMsgToWrite()
                        viewModel.setWritingState(NfcWritingState.Closed)
                    },
                )
            }
        }

        // 小米文档提供虚拟键透明方法，不干扰原生系统的手势提示线样式 TODO: 失效 补充：应是 HyperOS bug
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//        window.statusBarColor = Color.TRANSPARENT
//        window.navigationBarColor = Color.TRANSPARENT
//        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
//                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

        nfcManager = NfcManager(context = this)

        pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_MUTABLE // Important!!!
        )
        val tagDetected = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT)
        writeTagFilters = arrayOf(tagDetected)

        // For when the activity is launched by the intent-filter for android.nfc.action.NDEF_DISCOVERED
        if (nfcManager?.isNfcIntent(intent.action ?: "") == true) {
            pendingNfcMessage = nfcManager?.readFromIntent(intent)
            // 只保存 receivedMsg，等待后续执行完绑定服务工作时再处理
        } else {
            // Other type of intent
        }

        createNotificationChannel()
    }

    // For reading the NFC when the app is already launched
    override fun onNewIntent(intent: Intent) {
        Log.d("TaskApp.MainActivity", "onNewIntent")

        super.onNewIntent(intent)
        setIntent(intent)
        if (nfcManager?.isNfcIntent(intent.action ?: "") == true) {
            pendingNfcMessage = nfcManager?.readFromIntent(intent)
            // 只保存 receivedMsg，等待后续执行完绑定服务工作时再处理
        } else {
            // Other type of intent
        }

        if (pendingNfcMessage != null) {
            // 未绑定服务且有服务在运行：应等待 onStart() 中绑定服务并在连接时处理 Intent message
            if (isBound || !isServiceRunning(TaskService::class.java)) {
                Log.d("TaskApp.MainActivity", "Intent handled by onNewIntent")
                handleNfcMessage()
            }
        }

        if (nfcManager?.isNfcTagIntent((intent.action ?: "")) == true) {
            val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            if (nfcManager?.hasPendingMsgToWrite() == true) {
                nfcManager?.writePendingMsg(tag, this)
                viewModel.setWritingState(NfcWritingState.Succeeded)
            }
        }
    }

    override fun onStart() {
        super.onStart()

        if (isServiceRunning(TaskService::class.java)) {
            Intent(this, TaskService::class.java).also { intent ->
                bindService(intent, connection, BIND_ABOVE_CLIENT)
            }
        } else {
            if (pendingNfcMessage != null) {
                Log.d("TaskApp.MainActivity", "Intent handled by onStart")
                handleNfcMessage()
            }
        }
    }

    public override fun onPause() {
        super.onPause()
        nfcManager?.writeModeOff(this)
    }

    public override fun onResume() {
        super.onResume()
        nfcManager?.writeModeOn(this, pendingIntent, writeTagFilters)
    }

    override fun onStop() {
        super.onStop()

        if (isBound) {
            unbindService(connection)
            isBound = false
            handler.removeCallbacks(runnable)  // 确保停止定期获取数据
        }
    }

    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    // TODO: 判断格式是否正确且是否是当前正在运行的任务，若是其他任务则提示是否终止或完成当前任务
    private fun handleNfcMessage() {
        lifecycleScope.launch {
            val nfcMsg: String
            if (pendingNfcMessage != null) {
                nfcMsg = pendingNfcMessage!!
                pendingNfcMessage = null

                val taskId = nfcMsg.toInt()
                val task = viewModel.getTask(taskId)

                if (task != null) { // 格式正确，成功找到任务
                    if (isBound || taskService?.hasTaskProcess == true) {
                        if (taskService?.taskId == task.id) { // ID 和当前进行中任务一致
                            finishTaskProcess()
                        } else {
                            // TODO: 警告并询问是否终止或完成当前任务
                        }
                    } else {
                        createTaskProcess(task)
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private val createTaskProcess: (task: Task) -> Unit = {
        if (taskService != null || isBound) {
            // 已存在未结束的任务，必须先结束
            // UI 或 NFC 模块需事先检查是否存在未结束任务，需警告是否终止先前任务（非 NFC 任务也可正常结束）
            // 故此处若仍存在绑定服务则提示错误信息
            Toast.makeText(this, "Already existing service found", Toast.LENGTH_LONG).show()
        } else {
            // 未绑定到服务则认为没有进行中的任务

            // 新建 Service 并绑定
            intent = Intent(this, TaskService::class.java).apply {
                putExtra("EXTRA_DATA_INT", it.id)
                putExtra("EXTRA_DATA_STRING", it.taskTitle)
            }
            startForegroundService(intent)
            bindService(intent, connection, BIND_ABOVE_CLIENT)
            isBound = true
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private val startOrContinueTask: () -> Unit = {
        if (taskService != null && isBound) {
            taskService!!.startOrContinueTask()
        } else {
            Toast.makeText(this, "Can't find service or service doesn't exist", Toast.LENGTH_LONG)
                .show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private val pauseTask: () -> Unit = {
        if (taskService != null && isBound) {
            taskService!!.pauseTask()
        } else {
            Toast.makeText(this, "Can't find service or service doesn't exist", Toast.LENGTH_LONG)
                .show()
        }
    }

    private val terminateTaskProcess: () -> Unit = {
        if (taskService != null && isBound) {
            taskService!!.terminateTaskProcess()

            handler.removeCallbacks(runnable)  // 停止定期获取数据
            viewModel.updateTaskProcessState(
                hasTaskProcess = false, isRunning = false, currentTaskTime = 0
            )

            unbindService(connection)
            isBound = false
            taskService = null
            intent = Intent(this, TaskService::class.java)
            stopService(intent)
        } else {
            Toast.makeText(this, "Can't find service or service doesn't exist", Toast.LENGTH_LONG)
                .show()
        }
    }

    private val finishTaskProcess: () -> Unit = {
        if (taskService != null && isBound) {
            val runningTime = taskService!!.finishTaskProcess()

            // TODO: 将完成任务信息保存并更新到数据库
            viewModel.updateTaskRecord(runningTime)

            handler.removeCallbacks(runnable)  // 停止定期获取数据
            viewModel.updateTaskProcessState(
                hasTaskProcess = false, isRunning = false, currentTaskTime = 0
            )

            unbindService(connection)
            isBound = false
            taskService = null
            intent = Intent(this, TaskService::class.java)
            stopService(intent)
        } else {
            Toast.makeText(this, "Can't find service or service doesn't exist", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "My Channel"
            val descriptionText = "Text channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("CHANNEL_ID", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system.
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
