// Imports
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

// Connection for managing the MQTT broker
public class Connection implements MqttCallback {

	private static final String DEFAULT_CLIENT_ID = "ID1";
	private static final String LOCAL_HOST = "tcp://localhost:1883";
	private static final String MQTT_TOPIC = "/tnk/dmg";
	private static MqttClient client;
	private static Tank tank;
	private static MemoryPersistence persistance = new MemoryPersistence();

	public void publish(String topic, MqttMessage message) {
		try{
			client.publish(topic, message);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void subscribe(MqttClient client) throws Exception {
		Connection connection = new Connection();
		client.setCallback(connection);
		client.connect();
		client.subscribe(MQTT_TOPIC);
	}

	public void unsubscribe(MqttClient client) {
		try{
			client.unsubscribe(MQTT_TOPIC);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void connectionLost(Throwable throwable) {
		System.out.println("Connection to the MQTT broker lost!");
	}

	@Override
	public void messageArrived(String s, MqttMessage mqttMessage) {
		try {
			tank.takeDamage();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
		System.out.println("Message delivered");
	}

//	 Main method for testing from Java server
	public static void main(String[] args) {
		try {
			client = new MqttClient(LOCAL_HOST, DEFAULT_CLIENT_ID, persistance);
			Connection connection = new Connection();
			connection.subscribe(client);
			tank = new Tank(client);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}

