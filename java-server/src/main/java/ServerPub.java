import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class ServerPub {

    private static final String CLIENTID2 = "ID2";
    private static final String LOCALHOST = "tcp://localhost:1883";
    private static final String TOPIC = "/tnk/cmd/atk";

    public static void main(String[] args) throws MqttException {

        MemoryPersistence persistance = new MemoryPersistence();

        MqttClient clientPub = new MqttClient(LOCALHOST, CLIENTID2, persistance);
        clientPub.connect();
        MqttMessage message = new MqttMessage();
        message.setPayload("Shooty".getBytes());
        clientPub.publish(TOPIC, message);
        clientPub.disconnect();
    }

}
