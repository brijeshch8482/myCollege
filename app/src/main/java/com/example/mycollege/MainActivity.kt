package com.example.mycollege

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.DownloadListener
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout


class MainActivity : AppCompatActivity() {
    var websiteURL = "https://bvcoend.ac.in/" // sets web url
    private lateinit var webview: WebView
    lateinit var mySwipeRefreshLayout: SwipeRefreshLayout
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        window.statusBarColor = Color.TRANSPARENT

        if (!CheckNetwork.isInternetAvailable(this)) //returns true if internet available
        {
            //if there is no internet do this
            setContentView(R.layout.activity_main)
            //Toast.makeText(this,"No Internet Connection, Chris",Toast.LENGTH_LONG).show();
            AlertDialog.Builder(this) //alert the person knowing they are about to close
                .setTitle("No internet connection available")
                .setMessage("Please Check you're Mobile data or Wifi network.")
                .setPositiveButton("Ok"
                ) { dialog, which -> finish() } //.setNegativeButton("No", null)
                .show()
        } else {
            //Webview stuff
            webview = findViewById(R.id.webView)
            webview.settings.javaScriptEnabled = true
            webview.settings.domStorageEnabled = true
            webview.settings.supportMultipleWindows()
            webview.overScrollMode = WebView.OVER_SCROLL_NEVER
            webview.loadUrl(websiteURL)
            webview.webViewClient = WebViewClientDemo()
        }

        webview.setDownloadListener(DownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        })

        //Swipe to refresh functionality
        mySwipeRefreshLayout = findViewById<View>(R.id.swipeContainer) as SwipeRefreshLayout
        mySwipeRefreshLayout.setOnRefreshListener { webview.reload() }
    }

    private inner class WebViewClientDemo : WebViewClient() {
        //Keep webview in app when clicking links
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            mySwipeRefreshLayout!!.isRefreshing = false
        }
    }

    //set back button functionality
    override fun onBackPressed() { //if user presses the back button do this
        if (webview.isFocused && webview.canGoBack()) { //check if in webview and the user can go back
            webview.goBack() //go back in webview
        } else { //do this if the webview cannot go back any further
            AlertDialog.Builder(this) //alert the person knowing they are about to close
                .setTitle("EXIT")
                .setMessage("Are you sure. You want to close this app?")
                .setPositiveButton("Yes"
                ) { dialog, which -> finish() }
                .setNegativeButton("No", null)
                .show()
        }
    }
}

internal object CheckNetwork {
    private val TAG = CheckNetwork::class.java.simpleName
    fun isInternetAvailable(context: Context): Boolean {
        val info =
            (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
        return if (info == null) {
            Log.d(TAG, "no internet connection")
            false
        } else {
            if (info.isConnected) {
                Log.d(TAG, " internet connection available...")
                true
            } else {
                Log.d(TAG, " internet connection")
                true
            }
        }
    }
}