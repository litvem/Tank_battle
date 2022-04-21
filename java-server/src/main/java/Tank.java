import org.eclipse.paho.client.mqttv3.MqttClient;

public class Tank {
	// Placeholder class for tank
	private MqttClient client;

	public Tank(MqttClient client) {
		// Constructor for tank
		this.client = client;
	}

	public void setSpeed(int speed) {
		// Sets tank speed
	}

	public void setHealth(int health) {
		// Sets tank health
	}

	public void takeDamage(int damage) {
		// Reduces health from damage
	}
}
