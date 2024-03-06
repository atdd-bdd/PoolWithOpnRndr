# Pool using OPENRNDR

This program is written using java.   You need to have java installed on your system.

This program has only been tested on Windows.   To run it on Windows, 
you can use either 

java -jar pool-with-openrndr-all.jar
or
pool.exe (a wrapper around the above)

You can play the game solo or with another person.    

To play with another person:

* Enter your id (e.g. email)
* Enter the other person's id 
* If you have both entered the ids correctly, the status will show as Connected 
* One of you will need to "Switch Turns" 
* You can communicate a simple message by entering in "Message to Opponent"

Whose ever turn it is can press "Cue Stroke" after setting the angle and force
* Lok for the opponent's status in the lower left corner. 
* If the other person's computer is much slower than yours, then it will show as "Moving", while yours may show as "Not Moving".  Don't hit Cue Stroke again until the opponent's status shows "Not Moving"

When the balls are not moving, you can click and move any of them

If you want to replay a shot, press  "Replay"

## Technical Issues

This project started with the OpenRndr template (https://guide.openrndr.org/setUpYourFirstProgram.html)  [OPENRNDR guide](https://guide.openrndr.org)

To run, download pool.exe 

The balls only have position and velocity 
An potential change is to add spin (vertical and horizontal).  This would affect the movement, collisions, and cushion rebounding.  This would be an interesting project.

pool3.kt contains main    
  The display and control methods 

physics.kt 
  The calculation methods 

Pockets.kt 
  Calculations for the pockets and cushions 

SaveAndLoad 
  Balls and configuration saving and loading 

The Gradle task to create a single jar of the program with all libraries is shadow 

To communicate with another player, the program uses a web service 

Here is additional information that was in the openrndr template. I have not tried these taks - just the Intellij run commands and creating a single jar.  

## Gradle tasks
 - `run` runs the TemplateProgram
 - `jar` creates an executable platform specific jar file with all dependencies
 - `zipDistribution` creates a zip file containing the application jar and the data folder
 - `jpackageZip` creates a zip with a stand-alone executable for the current platform (works with Java 14 only)

## Cross builds
To create runnable jars for a platform different from the platform you use to build one uses `./gradlew jar --PtargetPlatform=<platform>`. The supported platforms are `windows`, `macos`, `linux-x64` and `linux-arm64`. 

## Github Actions

This repository contains a number of Github Actions in `./github/workflows`. 
The actions enable a basic build run on commit, plus publication actions that are executed when
a commit is tagged with a version number like `v0.*` or `v1.*`.
