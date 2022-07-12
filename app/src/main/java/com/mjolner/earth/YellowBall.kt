package com.mjolner.earth

import android.graphics.Bitmap
import android.graphics.Canvas

class YellowBall(val bitmap: Bitmap,val x:Float,val y:Float) {
    fun draw(canvas: Canvas) {
        canvas.drawBitmap(bitmap,x, y,null)
    }
}