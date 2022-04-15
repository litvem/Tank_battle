#include <vector>

#include <Smartcar.h>
#include <WiFi.h>
#include <MQTT.h>
#include <OV767X.h>


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

unsigned long currentTime = millis();

ArduinoRuntime arduinoRuntime;
BrushedMotor leftMotor(arduinoRuntime, smartcarlib::pins::v2::leftMotorPins);
BrushedMotor rightMotor(arduinoRuntime, smartcarlib::pins::v2::rightMotorPins);
DifferentialControl control(leftMotor, rightMotor);

SimpleCar car(control);

std::vector<char> frameBuffer;

void setup()
{
  Serial.begin(9600);

  Camera.begin(QVGA, RGB888, 15);
  frameBuffer.resize(Camera.width() * Camera.height() * Camera.bytesPerPixel());

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
      lastShotTime = currentTime;
      //delay(500);
      //digitalWrite(shootyPin, LOW);
    } else if (topic == "/tnk/cmd/dir") {
      
      if (message == "N") { //Move forward
        car.setSpeed(fSpeed);
        car.setAngle(0);
        
      } else if (message == "S") { //Move backwards
        car.setSpeed(bSpeed);
        car.setAngle(0);
        
      } else if (message == "W") { //Turn left
        car.setSpeed(fSpeed);
        car.setAngle(-90);
        
      } else if (message == "E") { //Turn right
        car.setSpeed(fSpeed);
        car.setAngle(90);
        
      } else if (message == "NW") { //Turn left with 45 degrees moving forward
        car.setSpeed(fSpeed);
        car.setAngle(-45);
        
      } else if (message == "NE") { //Turn right with 45 degrees moving forward
        car.setSpeed(fSpeed);
        car.setAngle(45);
        
      } else if (message == "SW") { //Turn left with 45 degrees moving backwards
        car.setSpeed(bSpeed);
        car.setAngle(45);
        
      } else if (message == "SE") { //Turn right with 45 degrees moving backwards
        car.setSpeed(bSpeed);
        car.setAngle(-45);
        
      } else if (message == "X") { //Stop
        car.setSpeed(0);
        car.setAngle(0);
      }
    }
  });
}

void loop()
{
  
  if (mqtt.connected()) {
    mqtt.loop();
    currentTime = millis();
    static auto previousFrame = 0UL;
    if (currentTime - previousFrame >= 65) {
      previousFrame = currentTime;
      Camera.readFrame(frameBuffer.data());
      mqtt.publish("/tnk/vid", frameBuffer.data(), frameBuffer.size(),
                   false, 0);
    }
  }

  if ( currentTime == lastShotTime + SHOOT_RESET) { //If the pin is set to low immediatly after it was set to high, the tank won't shoot.
    digitalWrite(shootyPin, LOW);
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
      default: // if you receive something that you don't know, just stop
        car.setSpeed(0);
        car.setAngle(currentDegrees);
    }
  }
}
