package com.example.amcor

import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Build
import android.os.Bundle
import android.os.Debug
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.amcor.databinding.ActivityMainBinding
import com.example.amcor.ui.home.HomeFragment
import com.example.amcor.ui.home.HomeViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.scottyab.rootbeer.RootBeer
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.Charset


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    val homeViewModel: HomeViewModel by viewModels()

    private lateinit var nfcAdapter: NfcAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        // Adapta el contenido pantalla completa sin bordes
        enableEdgeToEdge()

        // Esconde el menú superior
        supportActionBar?.hide()

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val rootBeer = RootBeer(this)
/*
        if (rootBeer.isRooted) {
            // La aplicación se está ejecutando en un dispositivo rooteado
            Toast.makeText(this, "La aplicación no puede ejecutarse en un dispositivo rooteado.", Toast.LENGTH_SHORT).show()
            finish()
        } else if (isEmulator()) {
            // La aplicación se está ejecutando en un emulador, realiza las acciones correspondientes
            Toast.makeText(this, "La aplicación no puede ejecutarse en un emulador.", Toast.LENGTH_SHORT).show()
            finish()
        } else {*/
            nfcAdapter = NfcAdapter.getDefaultAdapter(this)

            val navView: BottomNavigationView = binding.navView

            val navController = findNavController(R.id.nav_host_fragment_activity_main)
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top-level destinations.
            val appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.navigation_home, R.id.navigation_qr
                )
            )

            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)

            handleIntent(intent)
        // }
    }

    // Cambia de fragmento home
    fun switchToHomeOption() {
        runOnUiThread {
            binding.navView.selectedItemId = R.id.navigation_home
        }
    }

    // Cambia de fragmento options
    fun switchToOptions() {
        runOnUiThread {
            binding.navView.selectedItemId = R.id.navigation_options
        }
    }

    fun analizarURL(url: String): Boolean {
        val enlace = url.takeIf { it.isNotEmpty() }?.let {
            if (!it.startsWith("https://")) {
                "https://$it"
            } else {
                it
            }
        }

        val perteneceAlDominio = enlace?.let {
            try {
                val uriEnlace = URL(it)
                val uriInicial = URL(homeViewModel.inicialUrl)

                // Comparar las partes relevantes de la URL (IP y ruta)
                val baseEnlace = uriEnlace.host
                val baseInicial = uriInicial.host

                baseEnlace == baseInicial
            } catch (e: MalformedURLException) {
                Log.e("Error al analizar la URL", e.message ?: "")
                false
            }
        } ?: false

        return perteneceAlDominio
    }

    private fun isEmulator(): Boolean {
        return (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk" == Build.PRODUCT
                || Debug.isDebuggerConnected()
                || isRunningInEmulatorByCpuInfo())
    }

    private fun isRunningInEmulatorByCpuInfo(): Boolean {
        try {
            val abi = Build.SUPPORTED_ABIS[0]
            if (abi.contains("x86") || abi.contains("x86_64")) {
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        intent?.let {
            val action = intent.action
            if (NfcAdapter.ACTION_NDEF_DISCOVERED == action) {
                val parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
                if (!parcelables.isNullOrEmpty()) {
                    val message = parcelables[0] as NdefMessage
                    val records = message.records
                    if (records.isNotEmpty()) {
                        val record = records[0]
                        val payload = record.payload
                        val text = String(payload, Charset.forName("UTF-8")).trim()
                        val startIndex  = text.indexOf(homeViewModel.inicialUrl)
                        val newUrlNfc = text.substring(startIndex)

                        if (analizarURL(newUrlNfc)) {
                            homeViewModel.currentUrl = newUrlNfc

                            // Reemplaza el fragmento actual con una nueva instancia de HomeFragment
                            val fragmentManager = supportFragmentManager
                            val homeFragment = HomeFragment()

                            // Reemplaza el fragmento y asegura que la transacción esté completa
                            fragmentManager.beginTransaction()
                                .replace(R.id.nav_host_fragment_activity_main, homeFragment)
                                .commit()
                            fragmentManager.executePendingTransactions()

                            // Llama directamente al método updateWebView en el fragmento recién añadido
                            (fragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as? HomeFragment)?.updateWebView(newUrlNfc)
                            switchToHomeOption()
                        }
                    }
                }
            }
        }
    }
}
