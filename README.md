# OrderKatya
This is a Kotlin project with Android SDK done. I use here: okhttp3, firebase-messaging, material design, gson, jackson, picasso, viewmodel
## Getting Started
~~You can clone that's app in Android Studio, but unfortuntely you cannot start that's app because it's missing two files: with const database and google-services. That's reason is security app. But dont worry, I'm working with Backender to create 'test' server.~~

You can clone the app in Android Studio. It use a test server to work. Unfortuntely the notifications are disabled, because there is no option in the app add new orders (viewing only), but don't worry, I work on this option

## Information
Project was created for company  [Katya RG Leotards](https://katya-rg.eu/) for helps in  management the orders. This app is connected with database and read from API informations about orders and informations about customers. Receive the notifications for every State, when app is turn off, turn on or in foreground.

## Technology
App was created with multiple libraries. To receive notification it uses [Firebase-messaging](https://firebase.google.com/docs/cloud-messaging). 
It's using [okhttp3](https://square.github.io/okhttp/) and [Gson](https://github.com/google/gson) to decipher and communication with database. 
Picture's are loading with [Picasso](https://square.github.io/picasso/)

## How it's work

When turned on, the application shows MainActivity with Fragments. The first fragment has recycler view with active customer orders. The second fragment has RV with archive orders (realized). The blue exclamation point inform that order is new. After clicking on the notification, you will be taken to the application with Activity wihich contains information about the order.


<p float="left">
<img src="https://user-images.githubusercontent.com/65240240/198530797-35e8fe91-2a81-40d9-9032-4a940351335e.png" alt="orderListImage" width="500"/>
<img src="https://user-images.githubusercontent.com/65240240/198531444-437728f8-64a9-4343-8192-53ea150c1a79.png" alt="orderListImage" width="500"/>

</p>

The next Activity displays the information about the selected order. It has the ability to change the order status from active to archived and vice versa. Items has inside recycler view. So when are more products, the view changes size by itself.
<p align="center">
<img src="https://user-images.githubusercontent.com/65240240/198532379-77b19660-bb3f-4cc6-906e-452996444cfa.png" alt="orderListImage" width="500"/>
</p>
