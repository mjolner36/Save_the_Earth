package com.mjolner.earth

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.view.SurfaceHolder


class GameThread(private val surfaceHolder: SurfaceHolder, gameView: GameView) : Thread() {
    private val gameView: GameView = gameView
    var isRunning = true


        @SuppressLint("WrongCall")
        override fun run() {
            var canvas: Canvas?
            while (isRunning) {
                canvas = null
                try {
                    canvas = surfaceHolder.lockCanvas()
                    synchronized(surfaceHolder) {
                        gameView.onDraw(canvas)
                        gameView.collisionsCheck(canvas)
                    }
                } finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas)
                    }
                }
            }
        }
}
