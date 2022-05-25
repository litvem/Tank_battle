package org.dit113group3.androidapp

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.*
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
    private var healthBar: ImageView? = null
    private var shootCooldown: ImageView? = null
    private var gameOverMessage: TextView? = null


    companion object {
        private var health = MAX_HEALTH
        private var TOKEN = ""
        private var PREFIX = "/tnk"
        private var SPEED_CONTROL = "$PREFIX/cmd/spd"
        private var DIRECTION_CONTROL = "$PREFIX/cmd/dir"
        private var SHOOT_CONTROL = "$PREFIX/cmd/atk"
        private var ELIMINATION = "$PREFIX/status/elim"
        private var HEALTH = "$PREFIX/status/hp"
        private var VIDEO = "$PREFIX/vid"
        private const val REQUEST_TOKEN = "/app/request"
        private const val SET_TOKEN = "/app/token/set"
        private const val TAG = "TankMqttController"
        private const val EXTERNAL_MQTT_BROKER = "aerostun.dev"
        private const val LOCALHOST = "10.0.2.2"
        private const val MQTT_SERVER = "tcp://$LOCALHOST:1883"
        private const val QOS = 0
        private const val IMAGE_WIDTH = 320
        private const val IMAGE_HEIGHT = 240
        const val SHOOT_COOLDOWN = 5000L
        private var cooldownCounter = SHOOT_COOLDOWN.toInt()
        private const val GAME_OVER = "GAME OVER"
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        mMqttClient = MqttClient(applicationContext, MQTT_SERVER, TAG)
        mCameraView = findViewById(R.id.imageView)
        healthBar = findViewById(R.id.health)
        gameOverMessage = findViewById(R.id.gameOver)

        val exit = findViewById<ImageButton>(R.id.exit)
        exit.setOnClickListener {
            // TODO: display main menu when it's ready
            val eBuilder = AlertDialog.Builder(this)
            eBuilder.setTitle("Exit")
            eBuilder.setIcon(R.drawable.ic_action_name)
            eBuilder.setMessage("Return to main menu?")
            eBuilder.setPositiveButton("RETURN") { Dialog,which->
                mMqttClient!!.publish(ELIMINATION, "", QOS, null)
                finish()
                exitProcess(0)
            }

            eBuilder.setNegativeButton("CANCEL") { dialog, which ->
            }
            val createBuild = eBuilder.create()
            createBuild.show()

        }

        shootCooldown = findViewById(R.id.coolDown)
        val shoot = findViewById<Button>(R.id.shoot)
        shoot.setOnClickListener {

            if (cooldownCounter == SHOOT_COOLDOWN.toInt()) {
                mMqttClient?.publish(SHOOT_CONTROL, "", QOS, null)

                // Reset cooldown
                cooldownCounter = 0
                object : CountDownTimer(SHOOT_COOLDOWN, SHOOT_COOLDOWN / SHOOT_EDGE) {
                    override fun onTick(millisUntilFinished: Long) {
                        cooldownCounter += (SHOOT_COOLDOWN / SHOOT_EDGE).toInt()
                        updateShootCooldown(shootCooldown, cooldownCounter)
                    }

                    override fun onFinish() {
                        cooldownCounter = SHOOT_COOLDOWN.toInt()
                        updateShootCooldown(shootCooldown, cooldownCounter)
                    }
                }.start()
            }
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
        if (TOKEN == "") {
            connectToMqttBroker()
        } else if (health == 0) {
            connectToTank()
            updateHealthBar(healthBar, health)
            gameOverMessage!!.text = GAME_OVER
        } else {
            connectToTank()
            updateHealthBar(healthBar, health)
            object : CountDownTimer(SHOOT_COOLDOWN - cooldownCounter, SHOOT_COOLDOWN / SHOOT_EDGE) {
                override fun onTick(millisUntilFinished: Long) {
                    cooldownCounter += (SHOOT_COOLDOWN / SHOOT_EDGE).toInt()
                    updateShootCooldown(shootCooldown, cooldownCounter)
                }

                override fun onFinish() {
                    cooldownCounter = SHOOT_COOLDOWN.toInt()
                    updateShootCooldown(shootCooldown, cooldownCounter)
                }
            }.start()
        }
    }

    //Connects to broker using a generic client ID and subscribes to the topic which a token is to be received.
    private fun connectToMqttBroker() {
        if (!isConnected) {

            mMqttClient?.connect(TAG, "", object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken) {
                        isConnected = true
                        val successfulConnection = "Connected to token assignment"
                        Log.i(TAG, successfulConnection)
                        Toast.makeText(applicationContext, successfulConnection, Toast.LENGTH_SHORT)?.show()

                        mMqttClient?.subscribe(SET_TOKEN, QOS, null)
                        mMqttClient?.publish(REQUEST_TOKEN, "", QOS, null)
                    }

                    override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                        val failedConnection = "Failed to connect to MQTT broker"
                        Log.e(TAG, failedConnection)
                        Toast.makeText(applicationContext, failedConnection, Toast.LENGTH_SHORT)?.show()
                    }
                }, object : MqttCallback {
                    override fun connectionLost(cause: Throwable) {
                        if (TOKEN == "") {
                            isConnected = false
                            val connectionLost = "Connection to MQTT broker lost"
                            Log.w(TAG, connectionLost)
                            Toast.makeText(applicationContext, connectionLost, Toast.LENGTH_SHORT)
                                ?.show()
                        }
                    }
                    @Throws(Exception::class)
                    override fun messageArrived(topic: String, message: MqttMessage) {
                        //once the message with the token arrives, the token will be saved and added to each topic.

                        TOKEN = message.toString()
                        DIRECTION_CONTROL = "/$TOKEN$DIRECTION_CONTROL"
                        SPEED_CONTROL = "/$TOKEN$SPEED_CONTROL"
                        SHOOT_CONTROL = "/$TOKEN$SHOOT_CONTROL"
                        ELIMINATION = "/$TOKEN$ELIMINATION"
                        HEALTH = "/$TOKEN$HEALTH"
                        VIDEO = "/$TOKEN$VIDEO"
                        PREFIX = "/$TOKEN$PREFIX"

                        Log.i(TAG, "[MQTT] Topic: $topic | Message: $message")

                        // Initialize the health bar and shoot button only if there's a tank connected
                        updateHealthBar(healthBar, MAX_HEALTH)
                        updateShootCooldown(shootCooldown, SHOOT_COOLDOWN.toInt())

                        //establishes a new connection, using the token as part of the client id. The new connection
                        //subscribes to topics related to a specific tank.
                        connectToTank()

                    }

                    override fun deliveryComplete(token: IMqttDeliveryToken) {
                        Log.d(TAG, "Message delivered")
                    }
                })
        }
    }

    private fun connectToTank() {
        mMqttClient = MqttClient(applicationContext, MQTT_SERVER, "App-$TOKEN")

        mMqttClient?.connect(TAG, "", object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken) {
                isConnected = true
                val successfulConnection = "Connected to MQTT broker"
                Log.i(TAG, successfulConnection)
                Toast.makeText(applicationContext, successfulConnection, Toast.LENGTH_SHORT)?.show()

                mMqttClient?.subscribe("$PREFIX/#", QOS, null)
                println("$PREFIX/#")
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
                if (topic == VIDEO) {
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
                } else if (topic == HEALTH) {
                    health = message.toString().toInt()
                    updateHealthBar(healthBar, health)
                } else if (topic == ELIMINATION) {
                    health = 0
                    updateHealthBar(healthBar, health)
                    gameOverMessage!!.text = GAME_OVER
                    mMqttClient?.unsubscribe("$PREFIX/#")
                } else {
                    Log.i(TAG, "[MQTT] Topic: $topic | Message: $message")
                }
            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {
                Log.d(TAG, "Message delivered")
            }
        })
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
}
