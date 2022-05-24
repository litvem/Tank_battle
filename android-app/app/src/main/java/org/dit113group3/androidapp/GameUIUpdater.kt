package org.dit113group3.androidapp

import android.graphics.Bitmap
import android.graphics.Color
import android.widget.ImageView
import kotlin.math.round

// -- Health bar --

// Dimensions of the health bar
private const val HEALTH_WIDTH = 250
private const val HEALTH_HEIGHT = 30

// Upper bounds of health bar thresholds
// Different colours will be displayed for each interval
private const val LOW_HEALTH = 0.25f
private const val MID_HEALTH = 0.5f
private const val HIGH_HEALTH = 0.75f
private const val HEALTHY = 1f

// Maximum health for a tank
const val MAX_HEALTH = 200

// Colours for the bar
private val LOW_COLOUR = Color.rgb(0xC4, 0x00, 0x00)
private val MID_COLOUR = Color.rgb(0xCE, 0xA2, 0x00)
private val HIGH_COLOUR = Color.rgb(0x8F, 0xC0, 0x00)
private val HEALTHY_COLOUR = Color.rgb(0x39, 0xBE, 0x00)
private val EMPTY = Color.rgb(0xA0, 0xA0, 0xA0)

fun updateHealthBar(healthBar: ImageView?, health: Int) {
    assert(health in 0..200)

    val healthPercent: Float = health.toFloat() / MAX_HEALTH
    val border: Int = round(HEALTH_WIDTH * healthPercent).toInt()

    val colour: Int = if (healthPercent > HIGH_HEALTH) HEALTHY_COLOUR
    else if (healthPercent > MID_HEALTH) HIGH_COLOUR
    else if (healthPercent > LOW_HEALTH) MID_COLOUR
    else LOW_COLOUR

    val bm: Bitmap = Bitmap.createBitmap(HEALTH_WIDTH, HEALTH_HEIGHT, Bitmap.Config.ARGB_8888)
    val colours: IntArray = IntArray(HEALTH_WIDTH * HEALTH_HEIGHT)

    colours.indices.forEach {
        val col: Int = it % HEALTH_WIDTH

        if (col <= border) colours[it] = colour
        else colours[it] = EMPTY
    }

    bm.setPixels(colours, 0, HEALTH_WIDTH, 0, 0, HEALTH_WIDTH, HEALTH_HEIGHT)
    healthBar!!.setImageBitmap(bm)
}

// -- Shoot cool down --

// Length of the shoot button's edge
const val SHOOT_EDGE = 100

// Colour used to fill the button
private val FILLER = Color.argb(0xA0, 0x64, 0x64, 0x64)

fun updateShootCooldown(shoot: ImageView?, millisPassed: Int) {
    assert(millisPassed in 0..5000)

    val cooldownPercentage: Float = millisPassed.toFloat() / MainActivity.SHOOT_COOLDOWN
    val border: Int = round(SHOOT_EDGE * cooldownPercentage).toInt()
    val colour: Int = FILLER

    val bm: Bitmap = Bitmap.createBitmap(SHOOT_EDGE, SHOOT_EDGE, Bitmap.Config.ARGB_8888)
    val colours: IntArray = IntArray(SHOOT_EDGE * SHOOT_EDGE)

    colours.indices.forEach {
        val row = SHOOT_EDGE - it / SHOOT_EDGE

        if (row <= border) colours[it] = colour
    }

    bm.setPixels(colours, 0, SHOOT_EDGE, 0, 0, SHOOT_EDGE, SHOOT_EDGE)
    shoot!!.setImageBitmap(bm)
}
