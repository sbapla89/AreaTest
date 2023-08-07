package com.example.applistlibrary

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager

class AppListRetriever {

    /**
     * Metodo per il recupero della lista delle app installate sul dispositivo.
     * */
    fun getAppList(ctx: Context): List<ApplicationInfo> {

        val packageManager: PackageManager = ctx.packageManager

        return packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
    }
}