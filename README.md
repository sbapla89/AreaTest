# AreaTest
Il progetto si suddivide in 3 moduli principali: App, AppListFeature e AppListLibrary.

# App
Modulo principale dell'app sviluppato con il pattern architetturale MVVM in cui la classe del viewmodel mette in collegamento le activity con la classe repository nel quale avviene il fetch dei dati. La Ui viene popolata e aggiornata attraverso gli observer che restituiscono in tempo reale tutti i cambiamenti a livello di dataset utilizzato. L'unica particolaritá del modulo é la SplashActivity nella quale é presente la configurazione e la logica per il download dinamico del modulo: AppListFeature descritto di seguito.    

# AppListFeature
Rappresenta una Dynamic Feature. La particolaritá di questa libreria é che, anche se presente nell'apk/bundle caricati sullo store, il modulo non viene scaricato con il resto dell'applicazione dall'utente. Per questo motivo nella SplashActivity é gestita una richiesta per effettuare il download a runtime del modulo. La libreria contiene una sua MainActivity che riproduce la logica della MainActivity presente nel modulo app.

# AppListLibrary
Modulo di backup introdotto poiché, per poter effettuare il download della dynamic feature, é neccassario che l'app sia caricata sulla play console almeno in un canale di test per poterne effettuare il download. All'avvio comparirá un dialog che mostrerá un codice di errore in caso di download non riuscito. Per utilizzare l'applicazione basterá procedere.
