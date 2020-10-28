# tweetToMap

## Introduction

A a simple application that displays tweets as pins on a map in real time according to a search term.

The app will consist of two views.
- The first one will host two elements: one input text field and a map that will display a pin for each tweet that is being produced in real time and that contains the text input by the user. Each pin should be displayed during a customizable lifespan.
-	The second screen will be displayed when the user clicks on any of the map pins. Then, the user should navigate to a new view, where the tweet’s details are shown.

**MVVM, DataBinding , Livedata, Retrofit2, Coroutine.**  

## Technical specifications


	- Kotlin programming language
	
	- Development enviroment:
	    - Android Studio 4.0.1
	    - Build #AI-193.6911.18.40.6626763, built on June 25, 2020
        - Runtime version: 1.8.0_242-release-1644-b01 amd64
        - VM: OpenJDK 64-Bit Server VM by JetBrains s.r.o
        - OS: Windows 10 10.0
		
	- SDK: minSdkVersion 26 , targetSdkVersion 30
	
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

## How to use

Put your API Keys (Twitter & Google Maps) in build.gradle(Module:twittApp.app)  line 19 to 28

        //GoogleMaps ApiKey
        manifestPlaceholders =[GoogleMapsApiKey:"PUT_YOUR_API_KEY"] //Google Maps Api Key

        //Tweeter Api Keys
        buildTypes.each {
            it.buildConfigField 'String', 'TwitterApiKey', '"PUT_YOUR_API_KEY"'
            it.buildConfigField 'String', 'TwitterApiSecret', '"PUT_YOUR_API_KEY"'
            it.buildConfigField 'String', 'TwitterAccessToken ', '"PUT_YOUR_API_KEY"'
            it.buildConfigField 'String', 'TwitterAccessTokenSecret ', '"PUT_YOUR_API_KEY"'
        }

How to get google maps Api key : &nbsp; https://developers.google.com/maps/documentation/android-sdk/get-api-key

How to get twitter Api Key:&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; https://developer.twitter.com/en/docs/apps/overview


## Story board

**Step 1:** Enter a word, select lifespan and press search

&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;<img src=https://github.com/antoniomy82/tweetToMap/blob/master/Screenshots/00_start.png>

***
**Step 2:** Click on a mark to see the information of its tweet.

&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;<img src=https://github.com/antoniomy82/tweetToMap/blob/master/Screenshots/01_run.png>

## Architecture


&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;<img src=https://github.com/antoniomy82/tweetToMap/blob/master/Screenshots/02_mvvm.png>

## Preliminary considerations

With the **free Twitter API Key**, you may have the following problems:

**A.	NO LOCATIONS**

In the 99,96% of the analysed tweets, there isn´t location data.
Therefore, to carry out the Project I have used simulated locations. 
For the cases where tweets have no location, I have assigned them a mock location.

**B.	RATE LIMITED**

When you make several request in a row, twitter denies you the service. 

See Twitter documentation:

https://developer.twitter.com/en/docs/twitter-api/v1/rate-limits#:~:text=Standard%20API%20v1.&text=You%20can%20only%20post%20300,id%20endpoint%20during%20that%20period.

To solve this problem, I catch tweets from 30 in 30. Even so, there are times when it gives the 420 error, if many requests are made in a row.

For the latter case, I show the previous tweets and don't start the request until after multiplying "lifetime" by 30, to increase the time until a new request is made.


&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;<img src=https://github.com/antoniomy82/tweetToMap/blob/master/Screenshots/03_420.png>
