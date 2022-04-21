package org.dit113group3.androidapp

import android.content.Context
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

class MqttClient(context: Context?, serverUrl: String?, clientId: String?) {
    private val mMqttAndroidClient: MqttAndroidClient
    fun connect(
        username: String?,
        password: String,
        connectionCallback: IMqttActionListener?,
        clientCallback: MqttCallback?
    ) {
        mMqttAndroidClient.setCallback(clientCallback)
        val options = MqttConnectOptions()
        options.setUserName(username)
        options.setPassword(password.toCharArray())
        options.setAutomaticReconnect(true)
        options.setCleanSession(true)
        try {
            mMqttAndroidClient.connect(options, null, connectionCallback)
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun disconnect(disconnectionCallback: IMqttActionListener?) {
        try {
            mMqttAndroidClient.disconnect(null, disconnectionCallback)
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun subscribe(topic: String?, qos: Int, subscriptionCallback: IMqttActionListener?) {
        try {
            mMqttAndroidClient.subscribe(topic, qos, null, subscriptionCallback)
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun unsubscribe(topic: String?, unsubscriptionCallback: IMqttActionListener?) {
        try {
            mMqttAndroidClient.unsubscribe(topic, null, unsubscriptionCallback)
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun publish(topic: String?, message: String, qos: Int, publishCallback: IMqttActionListener?) {
        val mqttMessage = MqttMessage()
        mqttMessage.setPayload(message.toByteArray())
        mqttMessage.setQos(qos)
        try {
            mMqttAndroidClient.publish(topic, mqttMessage, null, publishCallback)
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    init {
        mMqttAndroidClient = MqttAndroidClient(context, serverUrl, clientId)
    }
}
