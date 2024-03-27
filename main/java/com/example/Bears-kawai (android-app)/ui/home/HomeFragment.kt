package com.example.Bears-kawai.ui.home

import android.net.http.SslError
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.HttpAuthHandler
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.Bears-kawai.R


class HomeFragment : Fragment() {

    private lateinit var webView: WebView
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        webView = view.findViewById(R.id.webview)

        // Webview settings
        val settings: WebSettings = webView.settings

        // Enable java script in web view.
        settings.javaScriptEnabled=true
        webView.settings.setDomStorageEnabled(true)

        // Webview activa el boton de retroceso dentro
        webView.canGoBack()
        webView.setOnKeyListener(View.OnKeyListener { v , keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK

                && event.action == MotionEvent.ACTION_UP
                && webView.canGoBack()){
                webView.goBack()
                return@OnKeyListener true
            }
            false
        })

        // Webview se ejecuta en la app
        webView.webChromeClient = object : WebChromeClient() {

        }

        // Configurar el cliente de WebView para escuchar la finalización de la carga de la página
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                // Guardar la URL actual después de que la página se ha cargado completamente
                if (url != null) {
                    homeViewModel.currentUrl=url
                }
            }

            override fun onReceivedHttpAuthRequest(
                view: WebView?,
                handler: HttpAuthHandler?,
                host: String?,
                realm: String?
            ) {
                val username = homeViewModel.username
                val password = homeViewModel.pass

                handler?.proceed(username, password)
            }

            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                // Ignorar errores SSL
                handler?.proceed()
            }
        }

        if(!homeViewModel.qrCodeUrl.isNullOrEmpty()){
            if (homeViewModel.qrCodeUrl == "Error"){
                Toast.makeText(requireContext(), "Error codigo qr no valido", Toast.LENGTH_SHORT).show()
                webView.loadUrl(homeViewModel.currentUrl)
            } else {
                webView.loadUrl(homeViewModel.qrCodeUrl!!)
                homeViewModel.qrCodeUrl = null
            }
        } else {
            webView.loadUrl(homeViewModel.currentUrl)
        }

        return view
    }

    //Comprueba si estas en la pagina inicial con login
    private fun isOnHomePage(url: String?): Boolean {
        return url == homeViewModel.inicialUrl
    }
}

