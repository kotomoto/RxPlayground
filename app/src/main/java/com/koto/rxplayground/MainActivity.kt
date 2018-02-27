package com.koto.rxplayground

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName
    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        get_async_btn.setOnClickListener {
            performAsyncGet()
        }

        get_btn.setOnClickListener {
            reactiveGet()
        }

        generate_numbers_btn.setOnClickListener {
            generateNumbers()
        }
    }

    private fun reactiveGet() {
        disposable = Single.create<Boolean> { emitter ->
            val response = performGet()
            if (response.isSuccessful) {
                emitter.onSuccess(true)
            } else {
                emitter.onError(IllegalStateException("Http error with ${response.code()}"))
            }
        }.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ value ->
                    Log.i(TAG, "got value $value")
                }, { err ->
                    Log.i(TAG, "got error $err")
                })

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

    private fun generateNumbers() {
        Observable.create<Int> { e ->
            for (i in 1..10) {
              e.onNext(Random().nextInt(10))
            }
            e.onComplete()
        }.delay(2, TimeUnit.SECONDS).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            nextInt ->
                            response_tv.text = response_tv.text.toString() + " $nextInt"
                        },
                        {
                            err ->
                        },
                        {
                            response_tv.text = response_tv.text.toString() + " done!"
                        }
                )
    }

    override fun onStop() {
        super.onStop()
        disposable?.dispose()
    }
}
