#include <Smartcar.h>
#include <WiFi.h>
#include <MQTT.h>

MQTTClient mqtt;
WiFiClient net;

const char ssid[] = "";
const char pass[] = "";

const auto mqttBrokerUrl = "127.0.0.1";

const int fSpeed   = 70;  // 70% of the full speed forward
const int bSpeed   = -70; // 70% of the full speed backward
const int incrementalDegrees = 5; // degrees to turn
int currentDegrees = 0;

const int shootyPin = 250; //The emulator already have the shooting implemented in godot.
//To shoot it, is only required to set the pin 250 to true.

unsigned long lastShotTime = 0;
const unsigned long SHOOT_RESET = 100;

unsigned long lastShotTime2 = 0;
unsigned long test = millis();

ArduinoRuntime arduinoRuntime;
BrushedMotor leftMotor(arduinoRuntime, smartcarlib::pins::v2::leftMotorPins);
BrushedMotor rightMotor(arduinoRuntime, smartcarlib::pins::v2::rightMotorPins);
DifferentialControl control(leftMotor, rightMotor);

SimpleCar car(control);

void setup()
{
  Serial.begin(9600);

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

  Serial.println("Connecting to MQTT broker");
  while (!mqtt.connect("arduino", "public", "public")) {
    Serial.print(".");
    delay(1000);
  }

  mqtt.subscribe("/tnk/cmd/#", 1);
  mqtt.onMessage([](String topic, String message) {
    if (topic == "/tnk/cmd/atk") {
      digitalWrite(shootyPin, HIGH);
      lastShotTime2 = test;
      //delay(500);
      //digitalWrite(shootyPin, LOW);
    }
  });
}

void loop()
{
  test = millis();
  if (mqtt.connected()) {
    mqtt.loop();
  }

  if ( test == lastShotTime2 + SHOOT_RESET) {
    digitalWrite(shootyPin, LOW);
  }
  handleInput();
}

void handleInput()
{ // handle serial input if there is any

  unsigned long currentTime = millis();
  if (currentTime == lastShotTime + SHOOT_RESET) {  //If the pin is set to low immediatly after it was set to high, the tank won't shoot.
    digitalWrite(shootyPin, LOW);
  }
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
        car.setSpeed(fSpeed);
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
        break;
      case 's': //shoot
        digitalWrite(shootyPin, HIGH);
        lastShotTime = currentTime;
        break;
      default: // if you receive something that you don't know, just stop
        car.setSpeed(0);
        car.setAngle(currentDegrees);
    }
  }
}
