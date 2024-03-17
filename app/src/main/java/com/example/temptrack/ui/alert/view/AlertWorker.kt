package com.example.temptrack.ui.alert.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.PixelFormat
import android.icu.util.Calendar
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.temptrack.R
import com.example.temptrack.data.database.DatabaseClient
import com.example.temptrack.data.database.FavoriteLocalDataSourceImo
import com.example.temptrack.data.model.RoomAlert
import com.example.temptrack.data.model.WeatherForecastResponse
import com.example.temptrack.data.network.RetrofitClient
import com.example.temptrack.data.network.datasource.WeatherRemoteDataSourceImpl
import com.example.temptrack.data.repositry.WeatherRepositoryImpl
import com.example.temptrack.databinding.AlarmDialogBinding
import com.example.temptrack.datastore.ENUM_NOTIFICATIONS
import com.example.temptrack.datastore.Enum_ALERT
import com.example.temptrack.datastore.SettingDataStorePreferences
import com.example.temptrack.util.convertDateToLong
import com.example.temptrack.util.convertTimeToLong
import com.example.temptrack.util.getDateToAlert
import com.example.temptrack.util.getTimeToAlert
import com.example.temptrack.util.isConnected
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

class AlertWorker(private val appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    companion object {
        const val CHANNEL_ID = "channelID"
    }
    val repo =  WeatherRepositoryImpl.getInstance(
        WeatherRemoteDataSourceImpl.getInstance(RetrofitClient.weatherApiService),
        FavoriteLocalDataSourceImo.getInstance(DatabaseClient.getInstance(applicationContext).favoriteDao())
    )
    val id = inputData.getString("ID")

    @SuppressLint("SuspiciousIndentation")
    override suspend fun doWork(): Result {
        val settingDataStorePreferences = SettingDataStorePreferences.getInstance(appContext)

        var systemTime = System.currentTimeMillis()
        systemTime = convertDateToLong(getDateToAlert(systemTime, "en"))
        val alertWorker = inputData.getString("alertWorker")
        var roomAlert = Gson().fromJson(alertWorker, RoomAlert::class.java)
        val lat= settingDataStorePreferences.getLatitude().first()
        val long=settingDataStorePreferences.getLongitude().first()

        if (checkTime(roomAlert)) {
            Log.i("TAG", "doWork: checked True ")
            var response: WeatherForecastResponse
            val apiClient = RetrofitClient.weatherApiService
            if (isConnected(appContext)) {
                Log.i("TAG", "doWork: isConnected : internet isConnected")
                runBlocking {
                    response = apiClient.getWeatherForecast(
                        30.2794938,32.2792794,"matric","en"
                    )
                }

                var desc: String = response.alerts?.get(0)?.event
                    ?: appContext.getString(R.string.no_alert_weather_is_fine)
                if (desc == "") desc = appContext.getString(R.string.no_alert_weather_is_fine)
                if (settingDataStorePreferences.getNotificationsPref().toString() == ENUM_NOTIFICATIONS.Enabled.toString()) {
                    Log.i("TAG", "doWork:  notification : ${settingDataStorePreferences.notificationsPrefFlow} ")
                    if (settingDataStorePreferences.getAlertPref().toString() == Enum_ALERT.NOTIFICATION.toString()) {
                        Log.i("TAG", "doWork: alertType : ${settingDataStorePreferences.alertPrefFlow}")
                        settingDataStorePreferences.getCountryNamePref().first()?.let { setupNotification(it, desc) }
                    } else {
                        Log.i("TAG", "doWork: alertType : ${settingDataStorePreferences.alertPrefFlow}")
                        GlobalScope.launch(Dispatchers.Main) {
                           settingDataStorePreferences.getCountryNamePref().first().let { SetupAlarm(appContext, desc, it).onCreate() }
                        }
                    }
                    repo.deleteAlert(roomAlert)
                    WorkManager.getInstance(appContext)
                        .cancelAllWorkByTag(roomAlert.dateFrom.toString() + roomAlert.dateTo.toString())
                }
            }
        } else {
            Log.i("TAG", "doWork: checked false ")

            repo.deleteAlert(roomAlert)
            WorkManager.getInstance(appContext)
                .cancelAllWorkByTag(roomAlert.dateFrom.toString() + roomAlert.dateTo.toString())

        }
        return Result.success()
    }
    private fun setupNotification(timezone: String, descriptions: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "channel_name"
            val descriptionText = "channel_description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = applicationContext
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        val alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.appicon)
            .setContentTitle(timezone)
            .setContentText(descriptions)
            .setSound(alarmSound)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(applicationContext)) {
            if (ActivityCompat.checkSelfPermission(
                    appContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(1, builder.build())
        }
    }
    class SetupAlarm(
        private val context: Context,
        private val description: String,
        private val country: String
    ) {
        lateinit var binding: AlarmDialogBinding
        private lateinit var customDialog: View
        private var mediaPlayer: MediaPlayer = MediaPlayer.create(context, R.raw.alarm)


        fun onCreate() {
            mediaPlayer.start()
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            customDialog = inflater.inflate(R.layout.alarm_dialog, null)
            binding = AlarmDialogBinding.bind(customDialog)
            initView()
            val LAYOUT_FLAG: Int = flag()
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val layoutParams: WindowManager.LayoutParams = params(LAYOUT_FLAG)
            windowManager.addView(customDialog, layoutParams)

        }

        private fun flag(): Int {
            val LAYOUT_FLAG: Int
            LAYOUT_FLAG = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            }
            return LAYOUT_FLAG
        }

        private fun params(LAYOUT_FLAG: Int): WindowManager.LayoutParams {
            val width = (context.resources.displayMetrics.widthPixels * 0.85).toInt()
            return WindowManager.LayoutParams(
                width,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_LOCAL_FOCUS_MODE,
                PixelFormat.TRANSLUCENT
            )
        }

        private fun initView() {
            binding.txtAlarmDesc.text = description
            binding.txtAlarmCountry.text = country
            binding.btnOkAlarm.setOnClickListener {

                close()
            }
        }
        private fun close() {
            try {
                (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).removeView(
                    customDialog
                )
                customDialog.invalidate()
                (customDialog.parent as ViewGroup).removeAllViews()
            } catch (e: Exception) {
                Log.d("Error", e.toString())
            }
            mediaPlayer.release()
        }
    }
    private fun checkTime(alert: RoomAlert): Boolean {
        val calendar = Calendar.getInstance()
        val currentTime2 = convertTimeToLong(
            getTimeToAlert( calendar.time.time,"en"))
        val currentDateInMillis = convertDateToLong( getDateToAlert( Date().time,"en"))
        Log.i("TAG", "checkTime: alert.dateFrom : ${alert.dateFrom}  \n alert.dateTo : ${alert.dateTo}  \n currentDateInMillis : ${currentDateInMillis} \n   currentTime2: $currentTime2 \n   alert.time: ${alert.time}")
        return (currentDateInMillis >= alert.dateFrom) && (currentDateInMillis <= alert.dateTo + 300000) && (currentTime2 >= alert.time)
    }
}
