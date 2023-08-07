package com.example.findappfeature.repos

import android.content.Context
import android.content.pm.PackageManager
import com.example.logapplication.models.AppItem
import kotlinx.coroutines.flow.update

class MyRepo {

    /**
     * Metodo per il recupero della lista delle app installate sul dispositivo.
     * */
    fun getAppList(context : Context) : List<AppItem> {

        val appList : MutableList<AppItem> = mutableListOf()

        for (applicationInfo in context.packageManager.getInstalledApplications(PackageManager.GET_META_DATA)){
            appList.add(AppItem(false, applicationInfo))
        }

        return appList
    }
}