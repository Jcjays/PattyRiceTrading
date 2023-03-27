# Patty Rice Trading
Patty Rice Trading is an Android application that is designed to facilitate the trading of rice between buyers and sellers. The app is built on the Model-View-ViewModel (MVVM) architecture and uses various libraries and best practices to ensure optimal performance and maintainability.

# Features
The app has several features that make it easy for buyers and sellers to connect and trade rice. Some of the key features of the app include:

* User Authentication: Users can sign up and log in to the app using their email and password. Authentication is handled using Firebase Authentication.

* User Profiles: Users can create a profile and provide information such as their name, address, and phone number.

* Rice Listings: Sellers can create listings for the rice they have available for sale. Listings include information such as the type of rice, quantity, price, and location.

* Search Functionality: Buyers can search for rice listings based on various criteria such as location, price, and rice type.

* Admin Page: Admin can provide simple access to other users such as enable/disable their accounts as well as changing their basic information.

* Sort functionality: User can sort the product based on the price or date which the product has been added.

* Logging - all the transaction are logged in firebase firestore as client requested.

# Architecture
The app is built using the Model-View-ViewModel (MVVM) architecture. This architecture separates the presentation layer (View) from the business logic (ViewModel) and the data model (Model). This separation of concerns makes the app easier to maintain and test.

The ViewModel layer uses coroutines to perform asynchronous tasks such as fetching data from Firebase Firestore. This ensures that the app remains responsive and doesn't freeze when performing long-running tasks.

# Libraries and Best Practices
The app uses various libraries and follows best practices to ensure optimal performance and maintainability. Some of these libraries and practices include:

* Firebase Firestore: This is the database used to store rice listings and user profiles.

* Firebase Crashlytics: This library is used to track crashes and errors in the app, which helps the developers identify and fix issues quickly.

* AirBnb Epoxy: This library is used to build complex and highly optimized RecyclerViews. This ensures that the app remains responsive even when displaying large amounts of data.

* Kotlin: The app is written entirely in Kotlin, which is a modern programming language that is more concise and expressive than Java.

* Material Design: The app follows the Material Design guidelines, which ensure a consistent and intuitive user interface.

* Jetpack Navigation: It is used for navigation in the app which is more clear and easy to understand than default navigation system. 

# Conclusion
Patty Rice Trading is an Android app that makes it easy for buyers and sellers to connect and trade rice. The app is built on the MVVM architecture and uses various libraries and best practices to ensure optimal performance and maintainability. With its intuitive user interface and powerful features, Patty Rice Trading is not deployed in Google Play as client requested.
