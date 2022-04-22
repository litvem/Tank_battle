package org.dit113group3.androidapp

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.joystickjhr.JoystickJhr
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttMessage


class MainActivity : AppCompatActivity() {
    private var mMqttClient: MqttClient? = null
    private var isConnected = false
    private var mCameraView: ImageView? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        var exit = findViewById<ImageButton>(R.id.exit)
        var shoot = findViewById<Button>(R.id.shoot)

        var joystickJhr = findViewById<JoystickJhr>(R.id.joystickMove)
        joystickJhr.setOnTouchListener { view, motionEvent ->
            joystickJhr.move(motionEvent)
            joystickJhr.angle()
            joystickJhr.distancia()

            true
        }

        mMqttClient = MqttClient(applicationContext, MQTT_SERVER, TAG)
        mCameraView = findViewById(R.id.imageView)
        connectToMqttBroker()


    }

    override fun onResume() {
        super.onResume()
        connectToMqttBroker()
    }

    override fun onPause() {
        super.onPause()
        mMqttClient!!.disconnect(object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken) {
                Log.i(TAG, "Disconnected from broker")
            }

            override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable?) {}
        })
    }

    private fun connectToMqttBroker() {
        if (!isConnected) {
            mMqttClient!!.connect(TAG, "", object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    isConnected = true
                    val successfulConnection = "Connected to MQTT broker"
                    Log.i(TAG, successfulConnection)
                    Toast.makeText(applicationContext, successfulConnection, Toast.LENGTH_SHORT)
                        .show()
                    //mMqttClient?.subscribe("/smartcar/ultrasound/front", QOS, null)
                    mMqttClient!!.subscribe("/tnk/vid", QOS, null)
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    val failedConnection = "Failed to connect to MQTT broker"
                    Log.e(TAG, failedConnection)
                    Toast.makeText(applicationContext, failedConnection, Toast.LENGTH_SHORT).show()
                }
            }, object : MqttCallback {
                override fun connectionLost(cause: Throwable) {
                    isConnected = false
                    val connectionLost = "Connection to MQTT broker lost"
                    Log.w(TAG, connectionLost)
                    Toast.makeText(applicationContext, connectionLost, Toast.LENGTH_SHORT).show()
                }

                @Throws(Exception::class)
                override fun messageArrived(topic: String, message: MqttMessage) {
                    if (topic == "/tnk/vid") {
                        val bm =
                            Bitmap.createBitmap(IMAGE_WIDTH, IMAGE_HEIGHT, Bitmap.Config.ARGB_8888)
                        val payload: ByteArray = message.getPayload()
                        val colors = IntArray(IMAGE_WIDTH * IMAGE_HEIGHT)
                        colors.indices.forEach { ci ->
                            val r = payload[3 * ci]
                            val g = payload[3 * ci + 1]
                            val b = payload[3 * ci + 2]
                            colors[ci] = Color.rgb(r.toInt(), g.toInt(), b.toInt())
                        }
                        bm.setPixels(colors, 0, IMAGE_WIDTH, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT)
                        mCameraView!!.setImageBitmap(bm)
                    } else {
                        Log.i(TAG, "[MQTT] Topic: " + topic + " | Message: " + message.toString())
                    }
                }

                override fun deliveryComplete(token: IMqttDeliveryToken) {
                    Log.d(TAG, "Message delivered")
                }
            })
        }
    }

    fun drive(throttleSpeed: Int, steeringAngle: Int, actionDescription: String?) {
        if (!isConnected) {
            val notConnected = "Not connected (yet)"
            Log.e(TAG, notConnected)
            Toast.makeText(applicationContext, notConnected, Toast.LENGTH_SHORT).show()
            return
        }
        Log.i(TAG, actionDescription!!)
        mMqttClient!!.publish(THROTTLE_CONTROL, Integer.toString(throttleSpeed), QOS, null)
        mMqttClient!!.publish(STEERING_CONTROL, Integer.toString(steeringAngle), QOS, null)
    }

    fun moveForward(view: View?) {
        drive(MOVEMENT_SPEED, STRAIGHT_ANGLE, "Moving forward")
    }

    fun moveForwardLeft(view: View?) {
        drive(MOVEMENT_SPEED, -STEERING_ANGLE, "Moving forward left")
    }

    fun stop(view: View?) {
        drive(IDLE_SPEED, STRAIGHT_ANGLE, "Stopping")
    }

    fun moveForwardRight(view: View?) {
        drive(MOVEMENT_SPEED, STEERING_ANGLE, "Moving forward left")
    }

    fun moveBackward(view: View?) {
        drive(-MOVEMENT_SPEED, STRAIGHT_ANGLE, "Moving backward")
    }

    companion object {
        private const val TAG = "SmartcarMqttController"
        private const val EXTERNAL_MQTT_BROKER = "aerostun.dev"
        private const val LOCALHOST = "10.0.2.2"
        private const val MQTT_SERVER = "tcp://" + LOCALHOST + ":1883"
        private const val THROTTLE_CONTROL = "/smartcar/control/throttle"
        private const val STEERING_CONTROL = "/smartcar/control/steering"
        private const val MOVEMENT_SPEED = 70
        private const val IDLE_SPEED = 0
        private const val STRAIGHT_ANGLE = 0
        private const val STEERING_ANGLE = 50
        private const val QOS = 1
        private const val IMAGE_WIDTH = 320
        private const val IMAGE_HEIGHT = 240
    }
}
