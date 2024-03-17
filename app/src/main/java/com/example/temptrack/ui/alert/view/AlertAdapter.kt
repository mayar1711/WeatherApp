package com.example.temptrack.ui.alert.view

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.temptrack.R
import com.example.temptrack.data.model.RoomAlert
import com.example.temptrack.databinding.AlertitemBinding
import com.example.temptrack.util.getDateToAlert
import com.example.temptrack.util.getTimeToAlert
import com.google.android.material.snackbar.Snackbar


class AlertAdapter(
    private var alertList: MutableList<RoomAlert>,
    var context: Context,
    var myListener: OnAlertListener
) : RecyclerView.Adapter<AlertAdapter.ViewHolder>() {
    private lateinit var binding: AlertitemBinding


    fun setList(list: List<RoomAlert>) {
        alertList = list as MutableList<RoomAlert>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding =AlertitemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = alertList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val current = alertList[position]

        holder.binding.txtFromDate.text = getDateToAlert(current.dateFrom,"en")
        holder.binding.txtToDate.text = getDateToAlert(current.dateTo,"en")
        holder.binding.txtFromTime.text = getTimeToAlert(current.time,"en")
        holder.binding.txtToTime.text = getTimeToAlert(current.time,"en")
        holder.binding.imageAlertDelete.setOnClickListener {
            val alertDialog = AlertDialog.Builder(context)

            alertDialog.apply {
                setIcon(R.drawable.baseline_delete_24)
                setTitle("Delete")
                setMessage("Are you sure you want to delete this alert ?")
                setPositiveButton("OK") { _: DialogInterface?, _: Int ->
                    myListener.alertDeleteClick(current)
                    Snackbar.make(
                        binding.root,
                        "The alert deleted successfully",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                setNegativeButton("Cancel") { _, _ ->
                }
            }.create().show()
        }
    }

    inner class ViewHolder(var binding: AlertitemBinding) : RecyclerView.ViewHolder(binding.root)

}

interface OnAlertListener {
    fun alertCardClick(alertObject: RoomAlert)
    fun alertDeleteClick(alertObject: RoomAlert)
}