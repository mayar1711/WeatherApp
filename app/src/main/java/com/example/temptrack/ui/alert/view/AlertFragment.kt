package com.example.temptrack.ui.alert.view

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.work.Constraints
import androidx.appcompat.app.AlertDialog
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.temptrack.MainActivity
import com.example.temptrack.R
import com.example.temptrack.data.database.DatabaseClient
import com.example.temptrack.data.database.FavoriteLocalDataSourceImo
import com.example.temptrack.data.model.RoomAlert
import com.example.temptrack.data.network.RetrofitClient
import com.example.temptrack.data.network.datasource.WeatherRemoteDataSourceImpl
import com.example.temptrack.data.repositry.WeatherRepositoryImpl
import com.example.temptrack.databinding.FragmentAlertBinding
import com.example.temptrack.datastore.Enum_ALERT
import com.example.temptrack.datastore.SettingDataStorePreferences
import com.example.temptrack.ui.alert.viewmodel.AlertViewModel
import com.example.temptrack.ui.alert.viewmodel.AlertViewModelFactory
import com.example.temptrack.ui.favorite.viewmodel.FavoriteViewModel
import com.example.temptrack.ui.favorite.viewmodel.FavoriteViewModelFactory
import com.example.temptrack.util.ResultCallBack
import com.example.temptrack.util.convertTimeToLong
import com.example.temptrack.util.getTimeToAlert
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class AlertFragment : Fragment() ,OnAlertListener{

    private lateinit var binding:FragmentAlertBinding
    private lateinit var viewModel: AlertViewModel
    private lateinit var factory: AlertViewModelFactory
    private lateinit var dataStorePreferences: SettingDataStorePreferences
    private lateinit var adapter: AlertAdapter
    private lateinit var countryName: String
    private var dateFrom: Long = 0
    private var dateTo: Long = 0
    private var time: Long = 0
    private lateinit var alertType: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding=FragmentAlertBinding.inflate(inflater,container,false)


        dataStorePreferences=SettingDataStorePreferences.getInstance(requireContext())
        alertType=dataStorePreferences.alertPrefFlow.toString()
        return binding.root
    }

    @SuppressLint("SuspiciousIndentation", "SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkOverlayPermission()
        adapter = AlertAdapter(ArrayList(), requireContext(), this)
        binding.recyclerViewAlert.adapter = adapter
        val repository = WeatherRepositoryImpl.getInstance(
            WeatherRemoteDataSourceImpl.getInstance(RetrofitClient.weatherApiService),
            FavoriteLocalDataSourceImo.getInstance(DatabaseClient.getInstance(requireContext()).favoriteDao()))
        factory= AlertViewModelFactory(repository)
        viewModel= ViewModelProvider(this,factory)[AlertViewModel::class.java]
        countryName=dataStorePreferences.getCountryNamePref().toString()


      lifecycleScope.launch() {
          viewModel.alertResponse.collectLatest { result ->
              when (result) {
                  is ResultCallBack.Loading -> {
                      binding.alertProgressBar.visibility = View.VISIBLE
                      binding.alertLottiAnimation.visibility = View.GONE
                      binding.recyclerViewAlert.visibility = View.GONE
                  }

                  is ResultCallBack.Success -> {
                      if (result.data.isNotEmpty()) {

                          binding.alertProgressBar.visibility = View.GONE
                          binding.alertLottiAnimation.visibility = View.GONE
                          binding.recyclerViewAlert.visibility = View.VISIBLE
                          adapter.setList(result.data)
                          adapter.notifyDataSetChanged()
                      } else {
                          binding.alertProgressBar.visibility = View.GONE
                          binding.alertLottiAnimation.visibility = View.VISIBLE
                          binding.recyclerViewAlert.visibility = View.GONE
                      }
                  }

                  is ResultCallBack.Error -> {
                      binding.alertProgressBar.visibility = View.VISIBLE
                      binding.alertLottiAnimation.visibility = View.GONE
                      binding.recyclerViewAlert.visibility = View.GONE
                      Snackbar.make(binding.root, result.message.toString(), Snackbar.LENGTH_LONG)
                          .show()
                  }
              }
          }
      }
            binding.addAlertFloating.setOnClickListener {
                Log.d("AlertFragment", "Add alert button clicked")
                val alertDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.alert_dialog, null)
                val alertBuilder = AlertDialog.Builder(requireContext()).setView(alertDialogView)
                    .setTitle(getString(R.string.setup_alert)).setIcon(R.drawable.baseline_add_alarm_24)
                val alertDialog = alertBuilder.show()
                Log.d("AlertFragment", "AlertDialog shown: $alertDialog")
                val fromDate: TextView = alertDialogView.findViewById(R.id.from_date)
                val toDate: TextView = alertDialogView.findViewById(R.id.to_date)
                val txtTime: TextView = alertDialogView.findViewById(R.id.time)
                alertDialogView.findViewById<MaterialButton>(R.id.btn_cancel_alert).setOnClickListener {
                    alertDialog.dismiss()
                }
                if (alertType == Enum_ALERT.NOTIFICATION.toString()) {
                    alertDialogView.findViewById<RadioGroup>(R.id.alert_type_radio_group)
                        .check(R.id.notif_radio_button)
                } else {
                    alertDialogView.findViewById<RadioGroup>(R.id.alert_type_radio_group)
                        .check(R.id.alarm_radio_button)
                }
                alertDialogView.findViewById<RadioGroup>(R.id.alert_type_radio_group)
                    .setOnCheckedChangeListener { group, checkedId ->
                        val alertTypetxt = alertDialogView.findViewById<View>(checkedId) as RadioButton
                            when (alertTypetxt.text) {
                                getString(R.string.notification) -> {
                                    lifecycleScope.launch {
                                        dataStorePreferences.setAlertPref(Enum_ALERT.NOTIFICATION)
                                    }
                                }
                                getString(R.string.alarm) -> {
                                    lifecycleScope.launch {
                                        dataStorePreferences.setAlertPref(Enum_ALERT.ALARM)
                                    }
                                }
                            }
                    }
                fromDate.setOnClickListener {
                    val c = Calendar.getInstance()
                    val year = c.get(Calendar.YEAR)
                    val month = c.get(Calendar.MONTH)
                    val day = c.get(Calendar.DAY_OF_MONTH)


                    val dpd = DatePickerDialog(
                        requireContext(), { view, year, monthOfYear, dayOfMonth ->

                            var dateString =
                                ("$dayOfMonth ${DateFormatSymbols(Locale.ENGLISH).months[monthOfYear]}, $year")
                            fromDate.text = dateString
                            val format = SimpleDateFormat("dd MMM, yyyy")
                            dateFrom = format.parse(dateString)!!.time
                        }, year, month, day
                    )
                    dpd.datePicker.minDate = c.timeInMillis;
                    dpd.show()
                }
                toDate.setOnClickListener {
                    val c = Calendar.getInstance()
                    val year = c.get(Calendar.YEAR)
                    val month = c.get(Calendar.MONTH)
                    val day = c.get(Calendar.DAY_OF_MONTH)


                    val dpd = DatePickerDialog(
                        requireContext(),
                        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                            var dateString =
                                ("$dayOfMonth ${DateFormatSymbols(Locale.ENGLISH).months[monthOfYear]}, $year")
                            toDate.text = dateString
                            val format = SimpleDateFormat("dd MMM, yyyy")
                            dateTo = format.parse(dateString).time
                        },
                        year,
                        month,
                        day
                    )

                    dpd.show()
                }
                txtTime.setOnClickListener {
                    val cal = Calendar.getInstance()
                    val timeSetListner = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                        cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        cal.set(Calendar.MINUTE, minute)
                        val am_or_pm: String = if (hourOfDay > 12) {
                            "PM"
                        } else {
                            "AM"
                        }
                        txtTime.text = SimpleDateFormat("hh:mm a").format(cal.time)
                        time = convertTimeToLong(SimpleDateFormat("hh:mm a").format(cal.time))
                    }
                    val tpd = TimePickerDialog(
                        requireContext(),
                        timeSetListner,
                        cal.get(Calendar.HOUR_OF_DAY),
                        cal.get(Calendar.MINUTE),
                        false
                    )

                    tpd.show()

                }
                alertDialogView.findViewById<MaterialButton>(R.id.btn_save_alert).setOnClickListener {
                    if (txtTime.text != "" && fromDate.text != "" && toDate.text != "") {
                        val roomAlertPojo = RoomAlert(
                            dateFrom = dateFrom,
                            dateTo = dateTo,
                            time = time,
                            countryName = countryName, description = ""
                        )
                        viewModel.insertAlert(roomAlertPojo)
                        adapter.notifyDataSetChanged()
                        setupWorker(roomAlertPojo)
                        alertDialog.dismiss()
                        Snackbar.make(
                            binding.root,
                            getString(R.string.alert_added_successfully),
                            Snackbar.LENGTH_LONG
                        ).show()

                    } else {
                        val alertDialog = AlertDialog.Builder(requireContext())
                        alertDialog.apply {
                            setIcon(R.drawable.info)
                            setTitle(getString(R.string.info))
                            setMessage(getString(R.string.enter_date_time))
                            setPositiveButton(getString(R.string.ok)) { _: DialogInterface?, _: Int ->
                            }
                        }.create().show()
                    }
                }

        }
    }
    override fun alertCardClick(alertObject: RoomAlert) {

    }

    override fun alertDeleteClick(alertObject: RoomAlert) {
        viewModel.deleteAlert(alertObject)
        adapter.notifyDataSetChanged()
    }
    fun setupWorker(roomalert: RoomAlert) {
        val calendar = java.util.Calendar.getInstance()
        val currentTime = convertTimeToLong(getTimeToAlert(calendar.timeInMillis, "en"))
        val targetTime = roomalert.time
        val initialDelay = targetTime - currentTime
        println(getTimeToAlert(currentTime, "en") + currentTime)
        println(getTimeToAlert(roomalert.time, "en") + targetTime)
        println(initialDelay)

        val data = Data.Builder()
        data.putString("address", roomalert.countryName)
        data.putLong("startDate", roomalert.dateFrom)
        data.putLong("endDate", roomalert.dateTo)
        var alert = Gson().toJson(roomalert)
        data.putString("alertWorker", alert)
        val workRequest = PeriodicWorkRequestBuilder<AlertWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .addTag(roomalert.dateFrom.toString() + roomalert.dateTo.toString())
            .setConstraints(
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            )
            .setInputData(data.build())
            .build()
        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
            roomalert.dateFrom.toString() + roomalert.dateTo.toString(),
            ExistingPeriodicWorkPolicy.REPLACE, workRequest
        )
    }

    private fun checkOverlayPermission() {
        if (!Settings.canDrawOverlays(requireContext())) {
            val alertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
            alertDialogBuilder.setTitle(getString(R.string.weather_alarm))
                .setMessage(getString(R.string.features))
                .setPositiveButton(getString(R.string.ok)) { dialog: DialogInterface, i: Int ->
                    var myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                    startActivity(myIntent)
                    dialog.dismiss()
                }.setNegativeButton(
                    getString(R.string.cancel)
                ) { dialog: DialogInterface, i: Int ->
                    dialog.dismiss()
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                }.show()
        }
    }

}