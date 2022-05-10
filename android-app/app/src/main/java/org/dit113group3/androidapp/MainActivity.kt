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
    private var mMqttClient: MqttClient? = null
    private var isConnected = false
    private var mCameraView: ImageView? = null

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
                            val r: Int = payload[3 * ci].toInt() and 0xFF
                            val g: Int = payload[3 * ci + 1].toInt() and 0xFF
                            val b: Int = payload[3 * ci + 2].toInt() and 0xFF
                            colors[ci] = Color.rgb(r, g, b)
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
        val angle: Float = processAngle(steeringAngle)
        val speed: Float = processSpeed(distFromOrigin, angle)
        // Final angle to be sent to the tank
        val direction: Float = processDirection(angle)
        mMqttClient?.publish(SPEED_CONTROL, speed.toString(), QOS, null)
        mMqttClient?.publish(DIRECTION_CONTROL, direction.toString(), QOS, null)
    }

    private fun processSpeed(distFromOrigin: Float, angle: Float): Float {
        val joystick: JoystickJhr = findViewById(R.id.joystickMove)

        // If position is on the lower half, invert speed
        val invert: Int = if (angle <= -90 || angle >= 90) -1 else 1

        return invert * distFromOrigin / (joystick.height / 2f)
    }

    private fun processAngle(steeringAngle: Float): Float {
        // Shifts 0 deg by 90 deg counterclockwise and makes the angle range from -180 to 180 deg
        // with negative values to the left and positive values to the right
        val angle = -((steeringAngle + 90) % 360 - 180)
        return angle
    }

    private fun processDirection(angle: Float): Float {
        // Left -> -1, right -> 1
        val generalDirection = if (angle < 0) -1 else 1

        // direction set to the opposite angle if reversing
        val direction: Float = if (angle <= -90 || angle >= 90) -(angle - generalDirection * 180)
                                else angle
        return direction / 90
    }

    companion object {
        private const val TAG = "TankMqttController"
        private const val EXTERNAL_MQTT_BROKER = "aerostun.dev"
        private const val LOCALHOST = "10.0.2.2"
        private const val MQTT_SERVER = "tcp://$LOCALHOST:1883"
        private const val PREFIX = "tnk"
        private const val SPEED_CONTROL = "/$PREFIX/cmd/spd"
        private const val DIRECTION_CONTROL = "/$PREFIX/cmd/dir"
        private const val SHOOT_CONTROL = "/$PREFIX/cmd/atk"
        private const val QOS = 1
        private const val IMAGE_WIDTH = 320
        private const val IMAGE_HEIGHT = 240
    }
}
