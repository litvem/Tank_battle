import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class Tank implements MqttCallback {

	private final String TANK_DESTROYED = "Tank destroyed!";
	private final int DAMAGE_TAKEN = 1;
	private int healthPoints = 200;
	private MqttClient client;
	private String token;
	MqttMessage currentHealth = new MqttMessage();
	MqttMessage tnkDestroyed = new MqttMessage();

	//Each tank in the java application is responsible to update its health points
	//and publish them. If the health points reach 0, the application shall publish
	//to the elim (elimination) topic.
	private final String[] MQTT_PUBLISH_TOPICS = {
			"/tnk/status/hp",
			"/tnk/status/elim"
	};

	private final String MQTT_SUBSCRIBE_TOPIC = "/tnk/dmg";

	public Tank(String token) throws MqttException {
		this.token = token;
		System.out.println(token);

		this.client = new MqttClient(Utils.LOCAL_HOST, "server" + token, new MemoryPersistence());
		this.client.setCallback(this);
		this.client.connect();
		this.client.subscribe("/" + this.token + MQTT_SUBSCRIBE_TOPIC);

	}

	//Deducts health points from the tank, then proceeds to publish updated healthpoints to the broker,
	//if healthPoints reaches 0, the client unsubscribes from "tnk/dmg" and publishes death message to the broker
	public void takeDamage() throws MqttException {
		healthPoints = healthPoints - DAMAGE_TAKEN;
		String updatedHealth = Integer.toString(healthPoints);
		System.out.println(updatedHealth);						//printing for testing purpose
		currentHealth.setPayload(updatedHealth.getBytes());
		this.client.publish("/" + this.token + MQTT_PUBLISH_TOPICS[0], currentHealth);

		if (healthPoints == 0) {
			System.out.println("Tank destroyed, GAME OVER!");	//printing for testing purpose
			this.client.unsubscribe(MQTT_SUBSCRIBE_TOPIC);
			tnkDestroyed.setPayload(TANK_DESTROYED.getBytes());
			this.client.publish("/" + this.token + MQTT_PUBLISH_TOPICS[1], tnkDestroyed);
		}
	}

	@Override
	public void connectionLost(Throwable throwable) {
		System.out.println("Connection between tank client and MQTT broker lost!");
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		try {
			this.takeDamage();
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {

	}

	public String getToken() {
		return token;
	}
}