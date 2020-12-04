package com.jl.tcs

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),TcsScoreListener {
    override fun onTCSScore(score: Int) {
        tv_score.text="$score"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_main)

        tcs_22.setOnClickListener { tcs_22.invalidate() }
        tcs_22.setTcsScoreListener(this)
        btn_restart.setOnClickListener {
            tcs_22.restart()
        }
        btn_up.setOnClickListener {
            tcs_22.changeDirection(0)
        }
        btn_down.setOnClickListener {
            tcs_22.changeDirection(-1)
        }
        btn_l.setOnClickListener {
            tcs_22.changeDirection(1)
        }
        btn_r.setOnClickListener {
            tcs_22.changeDirection(2)
        }
    }
}
