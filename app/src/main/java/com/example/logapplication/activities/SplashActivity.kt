package com.example.logapplication.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.logapplication.R
import com.google.android.play.core.splitinstall.SplitInstallException
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallErrorCode
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        val loadingModuleBar : ProgressBar = findViewById(R.id.moduloLoading)

        // Creata un'istanza dello SplitInstallManager per il download dinamico dei moduli.
        val splitInstallManager = SplitInstallManagerFactory.create(this)

        // Creata la request per il download dei moduli.
        val request = SplitInstallRequest
                .newBuilder()
                // Aggiunto il modulo desiderato per il download
                .addModule("FindAppFeature")
                .build()

        // Variabile che salva l'Id sessione della request.
        var mySessionId = 0

        // Listener per il request status.
        val listener = SplitInstallStateUpdatedListener { state ->
            if (state.sessionId() == mySessionId) {
                // Gestione dello stato.
                when (state.status()) {
                    SplitInstallSessionStatus.DOWNLOADING -> {
                        // Installazione modulo e mostro la progress bar
                        loadingModuleBar.visibility = View.VISIBLE
                    }
                    SplitInstallSessionStatus.INSTALLED -> {
                        // Modulo installato e navigazione verso l'activity del modulo
                        loadingModuleBar.visibility = View.GONE
                        // Navigazione verso la MainActivity del modulo scaricato
                        startActivity(Intent().setClassName("com.example", "com.example.findappfeature.MainActivity"))
                    }
                }
            }
        }

        // Registrazione del listener per tracciare i progressi del download.
        splitInstallManager.registerListener(listener)

        splitInstallManager
            // Invio della richiesta di installazione dei moduli.
            .startInstall(request)
            // Se il task viene completato gestisco lo status nel listener
            .addOnSuccessListener { sessionId -> mySessionId = sessionId }
            // Se il task fallisce gestisco l'errore
            .addOnFailureListener { exception ->
                when ((exception as SplitInstallException).errorCode) {
                    SplitInstallErrorCode.NO_ERROR -> {
                        loadingModuleBar.visibility = View.GONE
                        showErrorDialog(exception)
                    }
                    else -> {
                        loadingModuleBar.visibility = View.GONE
                        showErrorDialog(exception)
                    }
                }
            }
    }

    /**
     * Metodo per mostrare il dialog con l'eccezione riscontrata
     * */
    private fun showErrorDialog(exception: SplitInstallException) {

        AlertDialog.Builder(this)
            .setMessage(exception.message.toString())
            .setCancelable(false)
            .setPositiveButton(resources.getString(R.string.positive_button1)) { dialog, _ ->
                dialog.dismiss()
                goToMainPage()
            }
            .show()
    }

    /**
     * Navigazione verso la MainActivity del modulo app
     * */
    private fun goToMainPage(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}