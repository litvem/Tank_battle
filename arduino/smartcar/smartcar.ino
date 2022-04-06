#include <Smartcar.h>

const int fSpeed   = 50;  // 70% of the full speed forward
const int bSpeed   = -50; // 70% of the full speed backward
const int lDegrees = -90; // degrees to turn left
const int rDegrees = 90;  // degrees to turn right
const int goStraight = 0;


ArduinoRuntime arduinoRuntime;
BrushedMotor leftMotor(arduinoRuntime, smartcarlib::pins::v2::leftMotorPins);
BrushedMotor rightMotor(arduinoRuntime, smartcarlib::pins::v2::rightMotorPins);
DifferentialControl control(leftMotor, rightMotor);

SimpleCar car(control);

const int triggerPin           = 6; // D6
const int echoPin              = 7; // D7
const unsigned int maxDistance = 100;
SR04 front{arduinoRuntime, triggerPin, echoPin, maxDistance};

void setup()
{
    Serial.begin(9600);
}

void loop()
{
  const auto distance = front.getDistance();
  // When distance is `0` it means there's no obstacle detected
  if (distance > 0 && distance < 100) {

    car.setSpeed(bSpeed);
    delay(1500);
    car.setAngle(90);
    delay(1300);
    car.setAngle(goStraight);
    car.setSpeed(fSpeed);
  }
  
    handleInput();
    delay(1);
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
            car.setSpeed(fSpeed);
            car.setAngle(lDegrees);
            break;
        case 'r': // turn clock-wise
            car.setSpeed(fSpeed);
            car.setAngle(rDegrees);
            break;
        case 'f': // go ahead
            car.setSpeed(fSpeed);
            car.setAngle(0);
            break;
        case 'b': // go back
            car.setSpeed(bSpeed);
            car.setAngle(0);
            break;
        default: // if you receive something that you don't know, just stop
            car.setSpeed(0);
            car.setAngle(0);
        }
    }
}
