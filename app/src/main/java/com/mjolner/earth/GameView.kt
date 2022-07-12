package com.mjolner.earth

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt


class GameView(context: Context?) : SurfaceView(context),SurfaceHolder.Callback {
    var thread: GameThread? = null
    var t: Timer = Timer()
    var isGameOver:Boolean = false
    private val speed = 350
    var score = 0
    var playerRight:Player? = null
    var playerLeft:Player? = null
    var clickToMove = false
    var yellowBall:YellowBall? = null
    private var projectiles: ArrayList<Circle>? = null
    private var projectilesToRemove: ArrayList<Int>? = null
    var circleX:Double? = null
    var circleY:Double? = null
    var textPaint:Paint = Paint()

    init {
        holder.addCallback(this)
        thread = GameThread(holder, this)
        isFocusable = true
    }

    override fun surfaceCreated(p0: SurfaceHolder) {
        playerLeft = Player(width/2f - 80f, height * 0.8f - 80f)
        playerRight = Player(width/2 + 80f, height * 0.8f - 80f)
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.planet_earth)
        yellowBall = YellowBall(BitmapFactory.decodeResource(resources,R.drawable.planet_earth),width/2f - bitmap.width/2,height * 0.9f)

        circleX = yellowBall!!.bitmap.height.toDouble() / 2 + yellowBall!!.x
        circleY = yellowBall!!.bitmap.width.toDouble() / 2 + yellowBall!!.y

        thread?.isRunning = true
        if (thread!!.state == Thread.State.NEW) {
            thread!!.start();
        }
        projectiles = ArrayList<Circle>()
        projectilesToRemove = ArrayList()
        t = Timer()
        t!!.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                drawCircles(width)
            }
        }, 0, speed.toLong())
    }

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {}

    override fun surfaceDestroyed(p0: SurfaceHolder) {
        var retry = true
        while (retry) {
            try {
                thread!!.join()
                retry = false
            } catch (e: InterruptedException) {
            }
        }
    }

    public override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas != null) {
            canvas.drawColor(Color.BLACK)
            yellowBall!!.draw(canvas)
            textPaint.textSize = 54f
            textPaint.color = Color.WHITE
            canvas.drawText("High Score: ${(context as Activity).
            getPreferences(Context.MODE_PRIVATE).getInt("Saved HighScore",0)}",
                width/6f, height*0.04f, textPaint)
            if (isGameOver){
                val rect = Paint()
                rect.color = Color.GRAY
                var paint = Paint()
                paint.color = Color.WHITE
                paint.textSize = 70f
                canvas.drawRect(0F, (3 * (height / 7)).toFloat(),
                    width.toFloat(), (4 * (height / 7)).toFloat(), rect)
                canvas.drawText("Restart", ((width / 3)+80).toFloat(),
                    (7 * (height / 16)+180).toFloat(), paint)
                canvas.drawText("Score: $score", ((width / 3)+60).toFloat(),
                    7 * (height / 20).toFloat(), paint)
                paint.color = Color.GREEN
                paint.textSize = 100f
                canvas.drawText("Save the Earth", (width / 3) - 120f.toFloat(),
                     (height / 4).toFloat(), paint)
            } else {
            textPaint.color = 0xBAE8E8FF.toInt()
            textPaint.textSize = 750f
            textPaint.textAlign = Paint.Align.CENTER
            val yPos = (height / 2 - (textPaint.descent() + textPaint.ascent()) / 2)
            val xPos = width/2f
            canvas.drawText(score.toString(),xPos,yPos,textPaint)


            playerLeft!!.draw(canvas)
            playerRight!!.draw(canvas)


            synchronized(playerRight!!){
                if ((playerRight!!.x <= width/2f + 80f) && !clickToMove){
                    playerRight!!.xVelocity = 0
                    playerLeft!!.xVelocity = 0
                    clickToMove = false
                }
            }
            synchronized(projectiles!!) {
                try {
                    for (projectile in projectiles!!) {
                        projectile.draw(canvas)
                        if (projectile.y > height * 0.99){
                            val integer = Integer.valueOf(projectiles!!.indexOf(projectile))
                            projectilesToRemove!!.add(integer)
                        }
                    }
                } catch (e: ConcurrentModificationException) { }
                for (integer in projectilesToRemove!!) {
                    projectiles!!.removeAt(integer)
                }
                projectilesToRemove!!.clear()
            }
        }

        }
    }

    fun drawCircles(width:Int) {
        val rand = (1..2).shuffled().last()
        var bitmap:Bitmap? = null
        when(rand){
            1->  bitmap = BitmapFactory.decodeResource(resources, R.drawable.rock)
            2-> bitmap = BitmapFactory.decodeResource(resources, R.drawable.moon)
        }
        val projectile = Circle(bitmap!!,width/2f - bitmap.width/2, 0f)
        projectiles!!.add(projectile)
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            clickToMove = true
            playerLeft!!.xVelocity= 7
            playerRight!!.xVelocity=-7

            if (event.x >= 0 && event.x <= width && event.y >= 3 * (height / 8) && event.y <= 5 * (height / 8) && isGameOver) {
                resetGame()
            }
        }

        // stop the player from moving
        if (event.action == MotionEvent.ACTION_UP) {
            playerLeft!!.xVelocity = -7
            playerRight!!.xVelocity = 7
            clickToMove = false

        }
        return true
    }


    fun saveHighScore() {
        val sharedPref = (context as Activity).getPreferences(Context.MODE_PRIVATE)
        if (score > sharedPref.getInt("Saved HighScore",0)){
            val editor = sharedPref.edit()
            editor.putInt("Saved HighScore", score)
            editor.apply()
        }
    }


    fun collisionsCheck(canvas: Canvas?){
        try {
            for (projectile in projectiles!!) {
                var posX = projectile.bitmap.height.toDouble() / 2 + projectile.x
                var posY = projectile!!.bitmap.width.toDouble() / 2 + projectile.y
                if ((circleX!! - posX).pow(2) + (circleY!! - posY).pow(2) <= yellowBall!!.bitmap.height){
                    if (projectile.bitmap.sameAs(BitmapFactory.decodeResource(resources,R.drawable.moon))){
                        score+=1
                        val integer = Integer.valueOf(projectiles!!.indexOf(projectile))
                        projectilesToRemove!!.add(integer)
                    }
                    else{
                        saveHighScore()
                        isGameOver = true
                    }
                }
                if (sqrt((playerRight!!.x - posX).pow(2) + (playerRight!!.y - posY).pow(2)) < 80 + 50){
                    if (projectile.bitmap.sameAs(BitmapFactory.decodeResource(resources,R.drawable.moon))){
                        score-=1
                    }
                    val integer = Integer.valueOf(projectiles!!.indexOf(projectile))
                    projectilesToRemove!!.add(integer)
                }
            }
        } catch (e: ConcurrentModificationException) {}
    }


    fun resetGame() {
        projectiles!!.clear()
        projectilesToRemove!!.clear()
        thread!!.isRunning = true
        score = 0
        isGameOver = false
        t!!.cancel()
        t = Timer()
        t!!.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                drawCircles(width)
            }
        }, 0, speed.toLong())
    }
}
