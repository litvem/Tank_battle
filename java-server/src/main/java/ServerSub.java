import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class ServerSub implements MqttCallback {

    private static final String CLIENTID = "ID1";
    private static final String LOCALHOST = "tcp://localhost:1883";
    private static final String[] TOPICS = {"/tnk/mes/gyro" };

    public static void main(String[] args) throws MqttException {

        MemoryPersistence persistance = new MemoryPersistence();
        System.out.println("Let's start subscribing!");

        MqttClient clientSub = new MqttClient(LOCALHOST, CLIENTID, persistance);
        clientSub.setCallback((new ServerSub()));
        clientSub.connect();
        clientSub.subscribe(TOPICS);

    }

    @Override
    public void connectionLost(Throwable throwable) {
        System.out.println("Connection to the MQTT broker lost!");
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        String message = new String(mqttMessage.getPayload());
        System.out.println("Gyroscope current heading:\n" + message);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        //currently not used
    }
}