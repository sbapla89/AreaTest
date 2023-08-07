package com.example.logapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.logapplication.models.AppItem
import com.example.logapplication.R
import com.example.logapplication.viewModels.MyViewModel

/**
 * Adapter che gestisce la visualizzazione della lista delle applicazioni installate sul device.
 * */
class AppListAdapter (private val dataSet: List<AppItem>, private val viewModel : MyViewModel) : RecyclerView.Adapter<AppListAdapter.ViewHolder>(){

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var checkBox: CheckBox

        init {
            checkBox = view.findViewById(R.id.checkItem)
        }

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.adapter_list_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.checkBox.text = dataSet[position].appInfo.packageName
        viewHolder.checkBox.isChecked = dataSet[position].isChecked

        viewHolder.checkBox.setOnClickListener {
            dataSet[position].isChecked = !dataSet[position].isChecked
            viewModel.updateSelection(dataSet[position])
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
}