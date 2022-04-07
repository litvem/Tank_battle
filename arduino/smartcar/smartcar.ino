#include <Smartcar.h>

const int fSpeed   = 70;  // 70% of the full speed forward
const int bSpeed   = -70; // 70% of the full speed backward
const int incrementalDegrees = 5; // degrees to turn
int currentDegrees = 0;

ArduinoRuntime arduinoRuntime;
BrushedMotor leftMotor(arduinoRuntime, smartcarlib::pins::v2::leftMotorPins);
BrushedMotor rightMotor(arduinoRuntime, smartcarlib::pins::v2::rightMotorPins);
DifferentialControl control(leftMotor, rightMotor);

SimpleCar car(control);

void setup()
{
    Serial.begin(9600);
}

void loop()
{
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
        default: // if you receive something that you don't know, just stop
            car.setSpeed(0);
            car.setAngle(currentDegrees);
        }
    }
}