

#include <Smartcar.h>
#include <WiFi.h>
#include <MQTT.h>

#ifdef __SMCE__
#include <vector>
#include <OV767X.h>
#endif


MQTTClient mqtt;
WiFiClient net;

const char ssid[] = "";
const char pass[] = "";

const auto mqttBrokerUrl = "127.0.0.1";

const int maxAngle = 60;

const int maxSpeed   = 70;  // 70% of the full speed forward
const int bSpeed   = -70; // 70% of the full speed backward
const int incrementalDegrees = 5; // degrees to turn
int currentDegrees = 0;

//The emulator already have the shooting implemented in godot.
//To shoot it, is only required to set the pin 250 to true.
const int shootyPin = 250;

unsigned long lastShotTime = 0;
const unsigned long SHOOT_RESET = 100;

unsigned long prevGyroscopeMeasurement = 0; //Saves the last gyroscope's measurement time
int gyroscopeTimeInterval = 50;             //Time interval between gyroscope's measurement
int currentHeading;
int previousHeading;
int gyLimit = 10;                          //The minimum variation between two measurements that will be interpreted as an impact

unsigned long currentTime = millis();

const int TOKEN_LENGTH = 2;
String token = "";
char tokenChar[TOKEN_LENGTH];  //some methods require a parameter of type char[] and don't accept the type String

//Subscription topics related to token assignment
const char TOKEN[] = "/tnk/token/#";
const char TOKEN_SET[] = "/tnk/token/set";

//Subscription topics related to commands
const char COMMAND_TOPIC[] = "/tnk/cmd/#";
const char DIRECTION[] = "/tnk/cmd/dir";
const char SPEED[] = "/tnk/cmd/spd";
const char ATTACK[] = "/tnk/cmd/atk";
const char ELIMINATION[] = "/tnk/status/elim";

//Publishing related topics
const char REQUEST[] = "/tnk/request";
const char DAMAGE[] = "/tnk/dmg";
const char VIDEO[] = "/tnk/vid";
const int arrSize = sizeof("/") + sizeof(tokenChar) + sizeof(VIDEO) / sizeof(char);
char video_topic[arrSize] = "/"; //video topic that will be completed once a token is assigned

ArduinoRuntime arduinoRuntime;
BrushedMotor leftMotor(arduinoRuntime, smartcarlib::pins::v2::leftMotorPins);
BrushedMotor rightMotor(arduinoRuntime, smartcarlib::pins::v2::rightMotorPins);
DifferentialControl control(leftMotor, rightMotor);

SimpleCar car(control);

const int GYROSCOPE_OFFSET = 37;
GY50 gyro(arduinoRuntime, GYROSCOPE_OFFSET);

#ifdef __SMCE__
std::vector<char> frameBuffer;
#endif

void setup()
{
  Serial.begin(9600);

#ifdef __SMCE__
  Camera.begin(QVGA, RGB888, 15);
  frameBuffer.resize(Camera.width() * Camera.height() * Camera.bytesPerPixel());
#endif

  pinMode(shootyPin, OUTPUT);

  WiFi.begin(ssid, pass);
  mqtt.begin(mqttBrokerUrl, 1883, net);

  Serial.println("Connecting to WiFi...");
  auto wifiStatus = WiFi.status();
  while (wifiStatus != WL_CONNECTED && wifiStatus != WL_NO_SHIELD) {
    Serial.println(wifiStatus);
    Serial.print(".");
    delay(1000);
    wifiStatus = WiFi.status();
  }

  Serial.println(wifiStatus);

  connectToTokenAssignment();

  //Initialize both heading related variables
  gyro.update();
  previousHeading = gyro.getHeading();
  currentHeading = previousHeading;
  Serial.println(currentHeading);

}

void loop()
{

  if (mqtt.connected()) {
    mqtt.loop();
    currentTime = millis();

#ifdef __SMCE__
    static auto previousFrame = 0UL;
    if (currentTime - previousFrame >= 65) {
      previousFrame = currentTime;
      Camera.readFrame(frameBuffer.data());
      if (!(token == "")) {
        mqtt.publish(video_topic, frameBuffer.data(), frameBuffer.size(),
                     false, 0);
      }
    }
#endif

  }

  if ( currentTime == lastShotTime + SHOOT_RESET) { //If the pin is set to low immediatly after it was set to high, the tank won't shoot.
    digitalWrite(shootyPin, LOW);
  }

  if (currentTime - prevGyroscopeMeasurement > gyroscopeTimeInterval) {
    gyro.update();
    currentHeading = gyro.getHeading();

    int diff = abs(currentHeading - previousHeading);

    //Checks if the limit was reached and filters the case which the tank completes a normal full rotation
    if (diff > gyLimit && (360 - diff > gyLimit)) {
      Serial.println("Impact detected");
      Serial.println(diff);
      if (!(token == "")) {
        mqtt.publish("/" + token + DAMAGE, "damage report");
      }
    }
    previousHeading = currentHeading;
    prevGyroscopeMeasurement = currentTime;
  }

  handleInput();
}

void handleInput()
{ // handle serial input if there is any

  if (Serial.available())
  {
    char input = Serial.read(); // read everything that has been received so far and log down
    // the last entry
    switch (input)
    {
      case 'l': // rotate counter-clockwise going forward
        currentDegrees -= incrementalDegrees;
        car.setAngle(currentDegrees);
        break;
      case 'r': // turn clock-wise
        currentDegrees += incrementalDegrees;
        car.setAngle(currentDegrees);
        break;
      case 'f': // go ahead
        car.setSpeed(maxSpeed);
        car.setAngle(currentDegrees);
        break;
      case 'b': // go back
        car.setSpeed(bSpeed);
        car.setAngle(currentDegrees);
        break;
      case 'c': //go straight
        currentDegrees = 0;
        car.setAngle(currentDegrees);
        break;
      default: // if you receive something that you don't know, just stop
        car.setSpeed(0);
        car.setAngle(currentDegrees);
    }
  }
}

void setSpeed(String message)
{
  float speed = message.toFloat() * maxSpeed;
  car.setSpeed(round(speed));
}

void setDirection(String message)
{
  float angle = message.toFloat() * maxAngle;
  car.setAngle(round(angle));
}

void connectToTokenAssignment()
{
  //Connects with a default ClientID ("arduino") to get a token
  Serial.println("Waiting for token");
  while (!mqtt.connect("arduino", "public", "public")) {
    Serial.print(".");
    delay(1000);
  }

  //Publish a topic that will work as a token request made to the java application
  mqtt.publish(REQUEST, "new tank");

  // Sets the tank's id via the first mqtt connection
  mqtt.subscribe(TOKEN, 1);
  mqtt.onMessage([](String topic, String message) {
    if (topic == TOKEN_SET) {

      if (token == "") {
        token = message.substring(0, TOKEN_LENGTH);
        Serial.println(token);
      }

      //Ends the connection with generic id
      mqtt.disconnect();

      establishSpecificConnection();

    } 
  });
}

void establishSpecificConnection()
{
  //Extracts the token string's chars to an char[] required
  //by the MQTT.connect() method and one of the MQTT.publish()
  //overload
  for (int i = 0; i < TOKEN_LENGTH; i++) {
    tokenChar[i] = token.charAt(i);
  }
  strcat(video_topic, tokenChar);
  strcat(video_topic, VIDEO);


  //Establishes new connection with the token as the ClientId
  //so that each tank has its own connection.
  Serial.println("Connecting to MQTT broker");
  while (!mqtt.connect(tokenChar, "public", "public")) {
    Serial.print(".");
    delay(1000);
  }
  Serial.println("Connected.");

  //Subscribes to tank's specific topics
  mqtt.subscribe("/" + token + COMMAND_TOPIC, 1);
  mqtt.subscribe("/" + token + ELIMINATION, 1);
  mqtt.onMessage([](String topic, String message) {

    if (topic == "/" + token + ATTACK) {
      digitalWrite(shootyPin, HIGH);
      lastShotTime = currentTime;

    } else if (topic == "/" + token + DIRECTION) {
      setDirection(message);

    } else if (topic == "/" + token + SPEED) {
      setSpeed(message);
      
    } else if (topic == "/" + token + ELIMINATION){
      mqtt.unsubscribe("/" + token + COMMAND_TOPIC);
    }

  });
}
