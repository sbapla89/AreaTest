package com.example.findappfeature.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.Settings
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.findappfeature.R
import com.example.findappfeature.repos.MyRepo
import com.example.logapplication.adapters.AppListAdapter
import com.example.logapplication.adapters.LogListAdapter
import com.example.logapplication.models.LogItem
import com.example.logapplication.viewModels.MyViewModel
import com.google.android.play.core.splitcompat.SplitCompat
import kotlinx.coroutines.launch

/**
 * Ragionamento e funzionamento analogo alla MainActivity del modulo app.
 * L'activity ha accesso al viewmodel nel modulo app e recupera la lista delle applicazioni da una classe presente in questo modulo (a differenza della sua controparte nel modulo app)
 * */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        setContentView(R.layout.activity_main)

        val viewModel = MyViewModel()
        val repo = MyRepo()

        val button: Button = findViewById(com.example.logapplication.R.id.button)

        val recyclerView: RecyclerView = findViewById(com.example.logapplication.R.id.recyclerAppList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = AppListAdapter(repo.getAppList(this), viewModel)

        lifecycleScope.launch {
            viewModel.getSelection().collect{
                button.isEnabled = it.isNotEmpty()
            }
        }

        button.setOnClickListener { viewModel.createLog(this) }

        lifecycleScope.launch {
            viewModel.showLog().collect{
                if (it.isEmpty()){
                    return@collect
                }
                showLogDialog(it, viewModel)
            }
        }

        showPermissionDialog()
    }

    private fun showPermissionDialog() {

        AlertDialog.Builder(this)
            .setTitle(com.example.logapplication.R.string.permission_title)
            .setMessage(com.example.logapplication.R.string.permission_message)
            .setCancelable(false)
            .setPositiveButton(resources.getString(com.example.logapplication.R.string.garantisci_button)) { dialog, _ ->
                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                startActivity(intent)
                dialog.dismiss()
            }
            .setNegativeButton(resources.getString(com.example.logapplication.R.string.close_button)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showLogDialog(logItems: List<LogItem>, viewModel: MyViewModel) {

        val recyclerView = RecyclerView(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = LogListAdapter(logItems)

        AlertDialog.Builder(this)
            .setView(recyclerView)
            .setCancelable(false)
            .setPositiveButton(resources.getString(com.example.logapplication.R.string.button_ok)) { dialog, _ ->
                viewModel.clearSelection(this)
                dialog.dismiss()
            }
            .show()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)

        SplitCompat.installActivity(this)
    }
}