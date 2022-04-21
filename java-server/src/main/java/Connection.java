// Imports
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;

// Connection for managing the MQTT broker
public class Connection implements MqttCallback {
	/**  
	*
	*	Placeholder
	*
	*/

	private static final String DEFAULT_CLIENT_ID = "ID1";
	private static final String LOCAL_HOST = "tcp://localhost:1883";
	private static final String[] MQTT_TOPICS = {
		"/tnk/mes/gyro",
		"/tnk/cmd/atk"
	};

	private MqttClient client;

	private MemoryPersistence persistance = new MemoryPersistence();

	public Connection() throws Exception {
		this.client = new MqttClient(LOCAL_HOST, DEFAULT_CLIENT_ID, persistance);
	}

	// Placeholder publish method
	public void publish() {
		
	}

	// Placeholder subscribe method
	public void subscribe() {

	}

	public MqttClient getClient() {
		return this.client;
	}

	@Override
    public void connectionLost(Throwable throwable) {
        System.out.println("Connection to the MQTT broker lost!");
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        String message = new String(mqttMessage.getPayload());
        System.out.println("Received!\n" + message);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        //currently not used
    }

	// Main method for testing from Java server
	public static void main(String[] args) {
		
		try {
			Connection connection = new Connection();
			MqttClient client = connection.getClient();
			client.setCallback(connection);
			client.connect();
			client.subscribe(MQTT_TOPICS[1]);
			MqttMessage message = new MqttMessage();
			message.setPayload("Java Server: Health 100".getBytes());
			client.publish(MQTT_TOPICS[1], message);
			client.disconnect();
			client.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}