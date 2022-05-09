import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class Main {

    public static final String TOKEN_REQUEST = "/tnk/request";


    public static void main(String[] args) {
        try {
            MqttClient client = new MqttClient(Utils.LOCAL_HOST, "tokenHandler-listener", new MemoryPersistence());

            TokenHandler tokenHandler = new TokenHandler();
            client.setCallback(tokenHandler);

            client.connect();
            client.subscribe(TOKEN_REQUEST);

        } catch (MqttException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
