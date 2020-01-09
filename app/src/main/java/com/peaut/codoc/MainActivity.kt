package com.peaut.codoc

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.Toast
import com.peaut.stateview.StateView
import com.peaut.stateview.singleClick
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private var stateView: StateView? = null
    private var count = 0
    private val handle = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val arg = msg.arg1
            when (arg % 4) {1 -> {
                stateView?.showContent()
            }
                2 -> {
                    stateView?.showEmpty()

                }
                3 -> {
                    stateView?.showRetry()

                }
                0 -> {
                    stateView?.showLoading()
                }
            }
            count++

            val msg1 = this.obtainMessage()
            msg1.arg1 = count
            this.sendMessageDelayed(msg1, 2000)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setStateLayout()
        show_content.singleClick {
            stateView?.showContent()
            Log.e("stateView", "showContent")
        }
        show_empty.singleClick {
            stateView?.showEmpty()
            Log.e("stateView", "showEmpty")
        }
        show_retry.singleClick {
            stateView?.showRetry()
            Log.e("stateView", "showRetry")
        }
        show_loading.singleClick {
            stateView?.showLoading()
            Log.e("stateView", "showLoading")
        }
        thread {
            count++
            val msg = handle.obtainMessage()
            msg.arg1 = count
            handle.sendMessageDelayed(msg, 2000)
        }
    }

    private fun setStateLayout() {
        stateView = StateView.inject(findViewById(R.id.content_id))
        stateView?.setOnRetryClickListener {
            Toast.makeText(this,"点击了重试",Toast.LENGTH_LONG).show()
        }
    }
}
