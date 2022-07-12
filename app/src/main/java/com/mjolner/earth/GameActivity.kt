package com.mjolner.earth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class GameActivity : AppCompatActivity() {
    private var gv: GameView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        gv = GameView(this)
        setContentView(gv)
    }


    override fun onBackPressed() {
        onDestroy()
        var intent = Intent(this@GameActivity,MainActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }
}