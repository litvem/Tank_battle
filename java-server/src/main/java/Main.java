import org.eclipse.paho.client.mqttv3.MqttException;

public class Main {

    public static void main(String[] args) {
        try {
            new TokenHandler();
        } catch (MqttException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
