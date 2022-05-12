// Imports
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

// Connection for managing the MQTT broker
public class TokenHandler implements MqttCallback {
	public static final String TANK_TOKEN_REQUEST = "/tnk/request";
	public static final String APP_TOKEN_REQUEST = "/app/request";
	public static final String TANK_TOKEN_RESPONSE = "/tnk/token/set";
	public static final String APP_TOKEN_RESPONSE = "/app/token/set";

	private final List<Tank> tanks;
	private int currentTank;
	MqttClient client = new MqttClient(Utils.LOCAL_HOST, "tokenHandler-publisher", new MemoryPersistence());


	public TokenHandler() throws MqttException {
		this.client.setCallback(this);
		this.client.connect();
		client.subscribe(TANK_TOKEN_REQUEST);
		client.subscribe(APP_TOKEN_REQUEST);
		tanks = new ArrayList<>();
		currentTank = 0;
	}

	@Override
	public void connectionLost(Throwable throwable) {
		System.out.println("Connection to the MQTT broker lost!");
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws MqttException {
		if (topic.equals(TANK_TOKEN_REQUEST)) {
			String token = Utils.generateToken();
			Tank tank = new Tank(token);
			tanks.add(tank);
			MqttMessage tokenMessage = new MqttMessage(token.getBytes(StandardCharsets.UTF_8));

			this.client.publish(TANK_TOKEN_RESPONSE, tokenMessage);
		} else if (topic.equals(APP_TOKEN_REQUEST)) {
			String token = tanks.get(currentTank++).getToken();
			MqttMessage tokenMessage = new MqttMessage(token.getBytes(StandardCharsets.UTF_8));

			this.client.publish(APP_TOKEN_RESPONSE, tokenMessage);
		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		System.out.println("Message delivered");
	}

}

