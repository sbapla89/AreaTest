package com.example.logapplication.repos

import android.content.Context
import com.example.applistlibrary.AppListRetriever
import com.example.logapplication.models.AppItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class MyRepository {

    private val _appListLiveData = MutableStateFlow(emptyList<AppItem>())

    /**
     * Metodo per il recupero della lista delle app installate sul dispositivo.
     * */
    fun getAppList(context : Context){

        val appList : MutableList<AppItem> = mutableListOf()

        // La lista viene recuperata utilizzando il metodo contenuto nella classe del modulo AppListLibrary
        for (applicationInfo in AppListRetriever().getAppList(context)){
            appList.add(AppItem(false, applicationInfo))
        }

        _appListLiveData.update {appList}
    }

    // StateFlow da restituire al viewModel per renderlo osservabile nella MainActivity
    fun getAppList() : MutableStateFlow<List<AppItem>>{
        return _appListLiveData
    }
}