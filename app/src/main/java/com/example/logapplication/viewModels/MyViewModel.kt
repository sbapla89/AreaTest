package com.example.logapplication.viewModels

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Context.USAGE_STATS_SERVICE
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.logapplication.models.AppItem
import com.example.logapplication.models.LogItem
import com.example.logapplication.repos.MyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MyViewModel : ViewModel() {

    private val repo : MyRepository = MyRepository()

    private val mapSelection = hashMapOf<String, AppItem>()
    private val _selectedAppListLiveData = MutableStateFlow(emptyList<AppItem>())

    private val _logs = MutableStateFlow(emptyList<LogItem>())

    /**
     * Crea e restituisce una lista osservabile di app installate
     * */
    fun loadAppList(context: Context) : MutableStateFlow<List<AppItem>> {
        repo.getAppList(context)
        return repo.getAppList()
    }

    /**
     * Crea e mantiene aggiornata la selezione delle applicazioni di cui produrre il log
     * */
    fun updateSelection(item : AppItem){

        if(item.isChecked){
            mapSelection[item.appInfo.packageName] = item
        }
        else{
            mapSelection.remove(item.appInfo.packageName)
        }

        getSelection()
    }

    /**
     * Restituisce la selezione aggiornata
     * */
    fun getSelection() : MutableStateFlow<List<AppItem>>{

        val appList : MutableList<AppItem> = mutableListOf()

        for (key in mapSelection.keys){
            mapSelection[key]?.let { appList.add(it) }
        }

        _selectedAppListLiveData.update {appList}

        return _selectedAppListLiveData
    }

    /**
     * Crea i dati da passare alla custom view del dialog per visualizzare il log delle applicazioni selezionate
     * */
    fun createLog(context : Context){

        // Lancio di una nuova coroutine sul thread I/O
        viewModelScope.launch(Dispatchers.IO){
            // Manager con le statistiche di utilizzo di tutte le applicazioni
            val usageStatsManager = context.getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
            val usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, getStartTime(), System.currentTimeMillis())

            val logList : MutableList<LogItem> = mutableListOf()

            // Per ogni applicazione selezionata cerco le sue statistiche di utilizzo e le salvo in una data class per produrre il log
            for (key in mapSelection.keys){
                val appItem = mapSelection[key]

                val lastUsedTime = usageStatsList.find { it.packageName == appItem?.appInfo?.packageName }?.lastTimeUsed
                val lastUsedTimeString = if (lastUsedTime != null) {
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(lastUsedTime))
                } else {
                    "N/A"
                }

                val log = LogItem(appItem?.appInfo?.name ?: "N/A" , appItem?.appInfo?.packageName ?: "N/A", lastUsedTimeString)
                logList.add(log)
            }

            _logs.update { logList }
        }
    }

    /**
     * Data aggiornata a quella odierna per il recupero delle statistiche di utilizzo delle applicazioni
     * */
    private fun getStartTime(): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        return calendar.timeInMillis
    }

    fun showLog() : MutableStateFlow<List<LogItem>> {
        return _logs
    }

    /**
     * Dopo la consultazione effettuo un reset della selezione e aggiorno la UI
     * */
    fun clearSelection(context : Context){
        _logs.update { emptyList() }
        _selectedAppListLiveData.update { emptyList() }
        mapSelection.clear()

        repo.getAppList(context)
    }
}