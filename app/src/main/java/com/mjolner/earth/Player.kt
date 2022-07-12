package com.mjolner.earth

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint


class Player(var x: Float, val y: Float) {
    var xVelocity = 0
    var paint: Paint = Paint()

    fun draw(canvas: Canvas) {
        paint.color = Color.WHITE
        canvas.drawCircle(x, y, 80f,paint)
        x -= xVelocity
    }
}