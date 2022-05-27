![Arduino Build Status](https://github.com/DIT113-V22/group-03/actions/workflows/arduino-build.yml/badge.svg)
![Android CI Status](https://github.com/DIT113-V22/group-03/actions/workflows/android-ci.yml/badge.svg)
![Java CI with Maven](https://github.com/DIT113-V22/group-03/actions/workflows/java-maven-ci.yml/badge.svg)

# Group 3 - TankBattle

## What are we going to make?

Our plan is to create a multiplayer game where players control the tanks to battle other tanks. Each player will be able to drive their own tank and shoot the opposing tanks. Each tank will be controlled via a mobile application.

When a new game begins, each tank will have a fully loaded health bar. Every time the tank gets hit by a projectile, its health will be reduced. When the tank loses all its health, it is out of the game. 

The goal of the game is to destroy all the enemies and survive under their fire.

## Why are we going to make it?

Playing computer games has become an irreplaceable part of modern life for a while now. No matter the age, we all experience stress and hardship. Game such as **TankBattle** will allow players to escape from their daily routine and have some fun. 

While playing this game, users will be able to make their own choices and stimulate a quick decision making process. Since the game has a fast pace, players will improve their hand-eye coordination and reflexes. 

Last but not least, playing **TankBattle** will allow users to experience excitement and danger to a certain extent, all from the comfort of their chair[*](https://gamequitters.com/15-reasons-people-play-video-games/#:~:text=Autonomy%20or%20Independence&text=They%20have%20a%20reason%20to,influence%20or%20direction%20from%20others.).

## How we made it

To allow players control the tanks, an Android application was developed using **Kotlin** and built using **Gradle**.

The connection between the Android app and Arduino tank is managed via MQTT protocol. The MQTT broker that we used during development was a local **Mosquitto** broker.

To handle the game logic, a **Java** application, with **Maven** as the chosen building tool, was implemented. This application stores and updates the tanks' health points. The tank reports impact detections to the Java application and the second informs the updated health points to the android app so it can be displayed to the player. The Java application also handles the distribution of unique tokens to each tank and app pair so that multiple players can play against each other.

All clients involved in a MQTT communication must have a unique client ID. Therefore, all tank and app instances have a generic ID at the first. Once they receive a token, a new connection is established using the token as part of the id to ensure its uniqueness.

**SMCE-gd** handles the physics and environment of the game. It emulates cars/tanks that contain Arduino uno board supporting many attachments including a camera and gyroscope.

All tanks in the emulator are run with a **C++** sketch(an .ino file). Fast changes in the gyroscope's measurements are used to detect impact inflicted on a tank.

To create a new landscape for the emulated environment, we used an open-source 3D modeling software **Blender** and the **Godot** game engine.

## Demonstration

This [video](https://youtu.be/fK3lWxg_zFw) contains a demonstration of the whole application running.


## Resources:
* [Eclipse Mosquitto™](https://mosquitto.org/)
* [SMCE-gd](https://github.com/ItJustWorksTM/smce-gd)
* [Blender](https://www.blender.org/) 
* [Godot](https://godotengine.org/)
* [Texture for the new landscape](https://ambientcg.com/view?id=Rock017)

* Android app: 
    * [Gradle](https://gradle.org/)
    * [Kotlin Standard Library](https://kotlinlang.org/api/latest/jvm/stdlib/)
    * [Android Gradle plugin](https://developer.android.com/reference/tools/gradle-api)
    * [Eclipse Paho Android Service](https://www.eclipse.org/paho/index.php?page=clients/android/index.php)
    * [joystick-android-studio-para-arduino](https://github.com/jose-jhr/joystick-android-studio-para-arduino)
    * [Game logo](http://www.freelogodesign.org)
    * [Font](https://www.fontget.com/font/tudor-victors/)
    * [Moon image for splash screen](https://earthsky.org/earthsky-community-photos/entry/43930/)
    * [Tank image for splash screen](http://favpng.com/png_view/tank-tank-download-icon-png/8yar3Bvc)

* Tank sketch:
    * [Arduino](https://www.arduino.cc/reference/en/)
    * [smartcar_shield](https://github.com/platisd/smartcar_shield)
    * [arduino-mqtt](https://github.com/256dpi/arduino-mqtt)
    * [Arduino WiFi Library](https://www.arduino.cc/reference/en/libraries/wifi/)
    * [Arduino_OV767X library](https://www.arduino.cc/reference/en/libraries/arduino_ov767x/)

* Java application:
    * [Maven](https://maven.apache.org/)
    * [Eclipse Paho Java Client](https://www.eclipse.org/paho/index.php?page=clients/java/index.php)
    * [Apache Commons Lang](https://commons.apache.org/proper/commons-lang/)


## Installation and setup

Follow the installation and setup guide for TankBattle [here](https://github.com/DIT113-V22/group-03/wiki/Installation-and-setup)!


## Virtual Hardware Architecture

The emulated tank has an arduino uno board and contains a OmniVision OV7670 camera and a gyroscope.  


## Software's Architecture

The software architecture style used in this project was the publish-subscribe style, where all components publish topics to a broker and subscribe to topics. Among the reasons for this choice, it can be listed SMCE constraints and time constraints(the course is short and learning the MQTT protocol is fairly simple). To distribute unique identifiers to each tank and app instance pair we had to emulate a client server architecture style to the tanks and app instances can “request” a token and receive a token as answer to that request. It is not an actual client server architecture since it still is a communication based in publishing topics and subscribing to topics between the 2 components. 

**MQTT communication topics:**

- **SMCE Tank**
    - Publishes: 
        - /tnk/request (request for token)
        - /\<token>/tnk/dmg (impact detection)
        - /\<token>/tnk/vid (video stream)
    
    - Subscribes to:
        - /tnk/token/set (token)
        - /\<token>/tnk/cmd/# (all commands)
        - /\<token>/tnk/status/elim

- **Android App** 
    - Publishes:
        - /app/request (request for token)
        - /\<token>/tnk/cmd/dir (direction)
        - /\<token>/tnk/cmd/spd (speed)
        - /\<token>/tnk/cmd/atk (shoot)
        - /\<token>/tnk/status/elim (tank's elimination)

    - Subscribes to:
        - /app/token/set (token)
        - /\<token>/tnk/vid (video stream)
        - /\<token>/tnk/status/elim (tank's elimination)
        - /\<token>/tnk/status/hp (tank's health points)

- **Java Application** 
    - Publishes: 
        - /tnk/token/set (token)
        - /app/token/set (token)
        - /\<token>/tnk/status/elim (tank's elimination)
        - /\<token>/tnk/status/hp (tank's health points)

    - Subscribes to: 
        - /tnk/request (request for token)
        - /app/request (request for token)
        - /\<token>/tnk/dmg (impact detection)
