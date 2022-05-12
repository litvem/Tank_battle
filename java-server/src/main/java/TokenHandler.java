// Imports
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

// Connection for managing the MQTT broker
public class TokenHandler implements MqttCallback {
	public static final String TOKEN_REQUEST = "/tnk/request";
	public static final String TOKEN_RESPONSE = "/token/set";

	private final List<Tank> tanks = new ArrayList<>();
	MqttClient client = new MqttClient(Utils.LOCAL_HOST, "tokenHandler-publisher", new MemoryPersistence());


	public TokenHandler() throws MqttException {
		this.client.setCallback(this);
		this.client.connect();
		client.subscribe(TOKEN_REQUEST);
	}

	@Override
	public void connectionLost(Throwable throwable) {
		System.out.println("Connection to the MQTT broker lost!");
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws MqttException {
		String token = Utils.generateToken();
		Tank tank = new Tank(token);
		tanks.add(tank);
		MqttMessage tokenMessage = new MqttMessage(token.getBytes(StandardCharsets.UTF_8));

		this.client.publish(TOKEN_RESPONSE, tokenMessage);
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		System.out.println("Message delivered");
	}

}

