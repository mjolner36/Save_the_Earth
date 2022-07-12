package com.mjolner.earth

import android.graphics.Bitmap
import android.graphics.Canvas


class Circle(var bitmap:Bitmap,var x: Float, var y: Float) {

    fun draw(canvas: Canvas) {
        canvas.drawBitmap(bitmap,x, y, null)
        y += 10
    }
}