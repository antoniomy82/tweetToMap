# tweetToMap

## Introduction

A a simple application that displays tweets as pins on a map in real time according to a search term.

The app will consist of two views.
- The first one will host two elements: one input text field and a map that will display a pin for each tweet that is being produced in real time and that contains the text input by the user. Each pin should be displayed during a customizable lifespan.
-	The second screen will be displayed when the user clicks on any of the map pins. Then, the user should navigate to a new view, where the tweet’s details are shown.

## Technical specifications
- Kotlin programming language

- Development environment:

    - Android Studio 4.0.1
    - Build #AI-193.6911.18.40.6626763, built on June 25, 2020
    - Runtime version: 1.8.0_242-release-1644-b01 amd64
    - VM: OpenJDK 64-Bit Server VM by JetBrains s.r.o
    - SDK: minSdkVersion 23 , targetSdkVersion 30

- OS: Windows 10 10.0

- Libraries used:
  - play-services-maps:17.0.0' : To get maps.
  - retrofit:2.8.1 : Rest client.
  - converter-gson:2.8.1 : Serialization and deserialization between objects.
  - okhttp-signpost:1.1.0 : For signin OkHttp request.
  - twitter:3.1.1 & twitter-core:3.1.1 : To Access twitter.
  - picasso:2.71828 : To get images from URL.
  - hdodenhof:circleimageview:3.0.0 : To make the images circular.
  - lifecycle-extensions:2.2.0 : Lifecycle
  - kotlinx-coroutines-core:1.3.9 : Kotlin coroutines
