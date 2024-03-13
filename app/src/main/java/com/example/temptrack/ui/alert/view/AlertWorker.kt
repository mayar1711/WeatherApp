package com.example.temptrack.ui.alert.view

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class AlertWorker(private val appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        TODO("Not yet implemented")
    }


}
