package com.example.logapplication.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.logapplication.adapters.AppListAdapter
import com.example.logapplication.R
import com.example.logapplication.adapters.LogListAdapter
import com.example.logapplication.models.LogItem
import com.example.logapplication.viewModels.MyViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val ctx : Context = this

        val button: Button = findViewById(R.id.button)

        // View che contiene la lista dei package installati sull'applicazione
        val recyclerView: RecyclerView = findViewById(R.id.recyclerAppList)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val viewModel: MyViewModel by viewModels()

        // Observer che popola l'adapter appena viene recuperata la lista di tutte le applicazioni sul dispositivo
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loadAppList(ctx).collect {
                    recyclerView.adapter = AppListAdapter(it, viewModel)
                }
            }
        }

        // Observer che abilita il bottone per visualizzare il report appena vengono selezionate le applicazioni desiderate
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.getSelection().collect{
                    button.isEnabled = it.isNotEmpty()
                }
            }
        }

        // Produzione del report
        button.setOnClickListener { viewModel.createLog(ctx) }

        // Observer che mostra il dialog contenente il log prodotto da unáltra coroutine in un altro thread
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.showLog().collect{
                    if (it.isEmpty()){
                        return@collect
                    }

                    showLogDialog(it, viewModel)
                }
            }
        }

        showPermissionDialog()
    }

    /**
     * Dialog utile per navigare direttamente alla pagina delle impostazioni dell'app per garantire il permesso necessario a recuperare le informazioni sulle statistiche di utilizzo delle app installate
     * */
    private fun showPermissionDialog() {

        AlertDialog.Builder(this)
            .setTitle(R.string.permission_title)
            .setMessage(R.string.permission_message)
            .setCancelable(false)
            .setPositiveButton(resources.getString(R.string.garantisci_button)) { dialog, _ ->
                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                startActivity(intent)
                dialog.dismiss()
            }
            .setNegativeButton(resources.getString(R.string.close_button)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    /**
     * Dialog con una custom view per visualizzare il report delle app selezionate
     * */
    private fun showLogDialog(logItems: List<LogItem>, viewModel: MyViewModel) {

        val recyclerView = RecyclerView(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = LogListAdapter(logItems)

        AlertDialog.Builder(this)
            .setView(recyclerView)
            .setCancelable(false)
            .setPositiveButton(resources.getString(R.string.button_ok)) { dialog, _ ->
                //resetto la selezione alla chiusura del dialog e aggiorno la UI
                viewModel.clearSelection(this)
                dialog.dismiss()
            }
            .show()
    }
}