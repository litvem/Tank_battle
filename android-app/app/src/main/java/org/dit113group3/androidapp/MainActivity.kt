package org.dit113group3.androidapp

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.joystickjhr.JoystickJhr
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttMessage
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() {
    var mMqttClient: MqttClient? = null
    var isConnected = false
    var mCameraView: ImageView? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mMqttClient = MqttClient(applicationContext, MQTT_SERVER, TAG)
        mCameraView = findViewById(R.id.imageView)

        val exit = findViewById<ImageButton>(R.id.exit)
        exit.setOnClickListener {
            // TODO: display main menu when it's ready
            val eBuilder = AlertDialog.Builder(this)
            eBuilder.setTitle("Exit")
            eBuilder.setIcon(R.drawable.ic_action_name)
            eBuilder.setMessage("Are you sure you want to Exit ?")
            eBuilder.setPositiveButton("EXIT") { Dialog,which->
                mMqttClient!!.publish("/$PREFIX/status/elim", "", QOS, null)
                finish()
                exitProcess(0)
            }

            eBuilder.setNegativeButton("CANCEL") { Dialog,which->
            }
            var createBuild = eBuilder.create()
            createBuild.show()

        }

        val shoot = findViewById<Button>(R.id.shoot)
        shoot.setOnClickListener {
            mMqttClient?.publish("/$PREFIX/cmd/atk", "", QOS, null)

            // TODO: add an internal timer that matches the shoot command cooldown on the tank
            // TODO: add a visual representation of said timer in for of either displaying the remaining time in the cooldown or through a gauge
        }

        val joystickJhr = findViewById<JoystickJhr>(R.id.joystickMove)
        joystickJhr.setOnTouchListener { view, motionEvent ->
            joystickJhr.move(motionEvent)
            drive(joystickJhr.distancia(), joystickJhr.angle())

            true
        }
    }

    override fun onResume() {
        super.onResume()
        connectToMqttBroker()
    }

    private fun connectToMqttBroker() {
        if (!isConnected) {
            mMqttClient?.connect(TAG, "", object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    isConnected = true
                    val successfulConnection = "Connected to MQTT broker"
                    Log.i(TAG, successfulConnection)
                    Toast.makeText(applicationContext, successfulConnection, Toast.LENGTH_SHORT)?.show()
                    //mMqttClient?.subscribe("/smartcar/ultrasound/front", QOS, null)
                    mMqttClient?.subscribe("/$PREFIX/#", QOS, null)
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    val failedConnection = "Failed to connect to MQTT broker"
                    Log.e(TAG, failedConnection)
                    Toast.makeText(applicationContext, failedConnection, Toast.LENGTH_SHORT)?.show()
                }
            }, object : MqttCallback {
                override fun connectionLost(cause: Throwable) {
                    isConnected = false
                    val connectionLost = "Connection to MQTT broker lost"
                    Log.w(TAG, connectionLost)
                    Toast.makeText(applicationContext, connectionLost, Toast.LENGTH_SHORT)?.show()
                }

                @Throws(Exception::class)
                override fun messageArrived(topic: String, message: MqttMessage) {
                    if (topic == "/$PREFIX/vid") {
                        val bm =
                            Bitmap.createBitmap(IMAGE_WIDTH, IMAGE_HEIGHT, Bitmap.Config.ARGB_8888)
                        val payload: ByteArray = message.payload
                        val colors = IntArray(IMAGE_WIDTH * IMAGE_HEIGHT)
                        colors.indices.forEach { ci ->
                            val r = payload[3 * ci]
                            val g = payload[3 * ci + 1]
                            val b = payload[3 * ci + 2]
                            colors[ci] = Color.rgb(r.toInt(), g.toInt(), b.toInt())
                        }
                        bm.setPixels(colors, 0, IMAGE_WIDTH, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT)
                        mCameraView!!.setImageBitmap(bm)
                    } else if (topic == "/$PREFIX/status/hp") { // TODO: implement when hp has been implemented
                        println("Foo")
                    } else if (topic == "/$PREFIX/status/elim") {
                        println("Bar")
                    } else {
                        Log.i(TAG, "[MQTT] Topic: $topic | Message: $message")
                    }
                }

                override fun deliveryComplete(token: IMqttDeliveryToken) {
                    Log.d(TAG, "Message delivered")
                }
            })
        }
    }

    fun drive(distFromOrigin: Float, steeringAngle: Float) {
        if (!isConnected) {
            val notConnected = "Not connected (yet)"
            Log.e(TAG, notConnected)
            Toast.makeText(applicationContext, notConnected, Toast.LENGTH_SHORT).show()
            return
        }
        val direction: Direction = processDirection(steeringAngle)
        val speed: Float = processSpeed(distFromOrigin, direction)
        mMqttClient?.publish(SPEED_CONTROL, speed.toString(), QOS, null)
        mMqttClient?.publish(DIRECTION_CONTROL, direction.toString(), QOS, null)
    }

    private fun processSpeed(distFromOrigin: Float, direction: Direction): Float {
        val joystick: JoystickJhr = findViewById(R.id.joystickMove)

        val invert: Int = when (direction) {
            Direction.DOWN, Direction.DOWN_LEFT, Direction.DOWN_RIGHT -> -1
            else -> 1
        }

        return invert * distFromOrigin / (joystick.height / 2f)
    }

    private fun processDirection(steeringAngle: Float): Direction {
        val circle: Float = 360f
        // Shift angle to align with UP_RIGHT-RIGHT edge
        val angle: Float = (steeringAngle - 22.5f) % circle
        val interval: Float = 45f
        val direction: Direction

        if (0 <= angle && angle < interval) {
            direction = Direction.UP_RIGHT
        } else if (interval % circle <= angle && angle < (2 * interval) % circle) {
            direction = Direction.UP
        } else if ((2 * interval) % circle <= angle && angle < (3 * interval) % circle) {
            direction = Direction.UP_LEFT
        } else if ((3 * interval) % circle <= angle && angle < (4 * interval) % circle) {
            direction = Direction.LEFT
        } else if ((4 * interval) % circle <= angle && angle < (5 * interval) % circle) {
            direction = Direction.DOWN_LEFT
        } else if ((5 * interval) % circle <= angle && angle < (6 * interval) % circle) {
            direction = Direction.DOWN
        } else if ((6 * interval) % circle <= angle && angle < (7 * interval) % circle) {
            direction = Direction.DOWN_RIGHT
        } else {
            direction = Direction.RIGHT
        }

        return direction
    }

    companion object {
        val TAG = "TankMqttController"
        val EXTERNAL_MQTT_BROKER = "aerostun.dev"
        val LOCALHOST = "10.0.2.2"
        val MQTT_SERVER = "tcp://$LOCALHOST:1883"
        val PREFIX = "tnk"
        val SPEED_CONTROL = "/$PREFIX/cmd/spd"
        val DIRECTION_CONTROL = "/$PREFIX/cmd/dir"
        val SHOOT = "/$PREFIX/cmd/atk"
        val QOS = 1
        val IMAGE_WIDTH = 320
        val IMAGE_HEIGHT = 240
    }
}
