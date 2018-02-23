package com.koto.rxplayground

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        get_async_btn.setOnClickListener {
            performAsyncGet()
        }
    }

    private fun performAsyncGet() {
        val client = OkHttpClient()
        val request = Request.Builder()
                .url("http://www.vogella.com/index.html")
                .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    throw IOException("Unexpected code " + response)
                }

                val responseData = response.toString()
                runOnUiThread({ response_tv.text = responseData })
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
        })
    }

    private fun performGet(): Response {
        val client = OkHttpClient()
        val request = Request.Builder()
                .url("http://www.vogella.com/index.html")
                .build()
        return client.newCall(request).execute()
    }
}
