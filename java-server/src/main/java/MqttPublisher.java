// Imports
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

// MqttPublisher for publishing messages to MQTT broker
public class MqttPublisher {
	// Main method for testing publish from Java server
	public static void main(String[] args) {
		try {
			MqttClient client = new MqttClient("tcp://localhost:1883", MqttClient.generateClientId());
			client.connect();
			MqttMessage message = new MqttMessage();
			message.setPayload("Hello world from Java".getBytes());
			client.publish("iot_data", message);
			client.disconnect();
			client.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}