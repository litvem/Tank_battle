import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Tank {

	private final String TANK_DESTROYED = "Tank destroyed!";
	private final int DAMAGE_TAKEN = 1;
	private int healthPoints = 200;
	private MqttClient client;
	private String token;
	MqttMessage currentHealth = new MqttMessage();
	MqttMessage tnkDestroyed = new MqttMessage();
	Connection connection = new Connection();
	private final String[] MQTT_TOPICS = {
			"/tnk/status/hp",
			"/tnk/status/elim"
	};

	public Tank(MqttClient client) throws Exception {
		this.client = client;
	}

	//Deducts health points from the tank, then proceeds to publish updated healthpoints to the broker,
	//if healthPoints reaches 0, the client unsubscribes from "tnk/dmg" and publishes death message to the broker
	public void takeDamage() {
		healthPoints = healthPoints - DAMAGE_TAKEN;
		String updatedHealth = Integer.toString(healthPoints);
		System.out.println(updatedHealth);						//printing for testing purpose
		currentHealth.setPayload(updatedHealth.getBytes());
		connection.publish(MQTT_TOPICS[0], currentHealth);

		if (healthPoints == 0) {
			System.out.println("Tank destroyed, GAME OVER!");	//printing for testing purpose
			connection.unsubscribe(client);
			tnkDestroyed.setPayload(TANK_DESTROYED.getBytes());
			connection.publish(MQTT_TOPICS[1], tnkDestroyed);
		}
	}
}