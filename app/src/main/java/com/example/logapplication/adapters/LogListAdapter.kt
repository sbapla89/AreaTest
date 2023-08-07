package com.example.logapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.logapplication.R
import com.example.logapplication.models.LogItem

/**
 * Adapter che gestisce la visualizzazione del log delle applicazioni desiderate.
 * */
class LogListAdapter (private val dataSet: List<LogItem>) : RecyclerView.Adapter<LogListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var appName: TextView
        var packageName: TextView
        var date: TextView

        init {
            appName = view.findViewById(R.id.appName)
            packageName = view.findViewById(R.id.packageName)
            date = view.findViewById(R.id.date)
        }

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.adapter_log_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        viewHolder.appName.text = dataSet[position].appName
        viewHolder.packageName.text = dataSet[position].packageName
        viewHolder.date.text = dataSet[position].lastUsedTime
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
}