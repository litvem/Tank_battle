// Imports
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

// Connection for managing the MQTT broker
public class TokenHandler implements MqttCallback {
	public static final String TOKEN_RESPONSE = "/token/set";

	private final List<Tank> tanks = new ArrayList<>();
	MqttClient client = new MqttClient(Utils.LOCAL_HOST, "tokenHandler-publisher", new MemoryPersistence());


	public TokenHandler() throws MqttException {
		this.client.connect();
	}

	@Override
	public void connectionLost(Throwable throwable) {
		System.out.println("Connection to the MQTT broker lost!");
	}

	@Override
	public void messageArrived(String s, MqttMessage mqttMessage) throws MqttException {
		String token = Utils.generateToken();
		Tank tank = new Tank(token);
		tanks.add(tank);
		MqttMessage message = new MqttMessage(token.getBytes(StandardCharsets.UTF_8));

		this.client.publish(TOKEN_RESPONSE, message);
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
		System.out.println("Message delivered");
	}

}

