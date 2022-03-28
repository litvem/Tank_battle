# Group 3 - TankBattle

## What are we going to make?

Our plan is to create a multiplayer game where players control the tanks to battle other tanks. Each player will be able to drive their own tank and shoot the opposing tanks. Each tank will be controlled via specifically designed mobile application.

When a new game begins, each tank will have a fully loaded health bar. Every time the tank gets hit by the projectile, its life will be reduced. When the tank loses all its health, it is out of the game. And the player who controlled that tank sees the displayed message.

The goal of the game is to destroy all the enemies and survive under their fire.

## Why are we going to make it?

Playing computer games has become an irreplaceable part of modern life for a while now. No matter the age, we all experience stress and  hardship. Game such as **TankBattle** will allow players to escape from their daily routine and have some fun. 

While playing this game, users will be able to make their own choices and stimulate a quick decision making process. Since the game has a fast pace, players will improve their hand-eye coordination and reflexes. 

Last but not least, playing **TankBattle** will allow users to experience excitement and danger to a certain extent, all from the comfort of their chair[*](https://gamequitters.com/15-reasons-people-play-video-games/#:~:text=Autonomy%20or%20Independence&text=They%20have%20a%20reason%20to,influence%20or%20direction%20from%20others.).

## How are we going to make it?

To allow players control the tanks, an Android application will be developed using **XML**, **Kotlin**, **CSS** in Android Studio.

The connection between the Android app and Arduino tank will be handled by a server created using **Java** or **Kotlin**.

**C++** is intended to be used for behaviour modification of the tank. The combination of gyroscope and ultrasonic sensor feedback will be used for calculation of the damage received.

**Emulator SMCE GoDot** will handle physics and environment of the game. Also **GoDot** will be used to create a new landscape/game mode.
