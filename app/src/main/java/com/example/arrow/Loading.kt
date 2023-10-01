package com.example.arrow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast

class Loading : AppCompatActivity() {
    private val handler = Handler()
    private var progress = 0
    private var isConnected = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        val action: String? = intent?.action
        val data: Uri? = intent?.data

        data?.let {
            Log.i("MyTag", "$data")
        }




        val sbProgress = findViewById<SeekBar>(R.id.loadingBar)
        sbProgress.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                // Disable the dragging of the seekbar
                return false
            }
        })
        val name = findViewById<TextView>(R.id.tvConnection)
        val slogan = findViewById<TextView>(R.id.tvSlogan)

        isConnected = isInternetAvailable(this)
        if (isConnected) {
            name.text = "Arrow"
            slogan.text = "Aim and find your way"
            startProgressAutomation(sbProgress)
        } else {
            startProgressAutomation(sbProgress)
            name.text = "Unable to connect"
            Toast.makeText(this, "Check your connection", Toast.LENGTH_LONG).show()
        }
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)

        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    private fun startProgressAutomation(seekBar: SeekBar) {
        seekBar.max = 300
        handler.postDelayed({
            progress += 3
            seekBar.progress = progress
            if (progress >= 300) {
                if(isConnected) {
                    startActivity(Intent(this@Loading, Login::class.java))
//                    startActivity(Intent(this@Loading, Login::class.java))
                    finish();
                }
            } else {
                startProgressAutomation(seekBar)
            }
        }, 10) // Delay in milliseconds
    }
}