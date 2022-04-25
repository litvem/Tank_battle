

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

const int straight = 0;
const int fullRight = 90;
const int fullLeft = -90;
const int right = 45;
const int left = -45;

const int maxSpeed   = 70;  // 70% of the full speed forward
const int bSpeed   = -70; // 70% of the full speed backward
const int incrementalDegrees = 5; // degrees to turn
int currentDegrees = 0;

const int shootyPin = 250; //The emulator already have the shooting implemented in godot.
//To shoot it, is only required to set the pin 250 to true.

unsigned long lastShotTime = 0;
const unsigned long SHOOT_RESET = 100;

unsigned long prevGyroscopeMeasurement = 0; //Saves the last gyroscope's measurement time
int gyroscopeTimeInterval = 50; //Time interval between gyroscope's measurement
int currentHeading;
int previousHeading;
int gyLimit = 10; //The minimum variation between two measurements that will be interpreted as an impact

unsigned long currentTime = millis();

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

    } else if (topic == "/tnk/cmd/dir") {
      setDirection(message);

    } else if (topic == "/tnk/cmd/spd") {
      setSpeed(message);
    }
  });

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
      mqtt.publish("/tnk/vid", frameBuffer.data(), frameBuffer.size(),
                   false, 0);
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
      mqtt.publish("/tnk/dmg", "1");
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
  if (message == "N" || message == "S") { //Move straight
    car.setAngle(straight);

  } else if (message == "W") { //Turn left with 90 degrees
    car.setAngle(fullLeft);

  } else if (message == "E") { //Turn right with 90 degrees
    car.setAngle(fullRight);

  } else if (message == "NW" || message == "SW") { //Turn left with 45 degrees
    car.setAngle(left);

  } else if (message == "NE" || message == "SE") { //Turn right with 45 degrees
    car.setAngle(right);
  }
}
