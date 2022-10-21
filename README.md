# Justas Onboarding App

### Screens
- [x] Splash screen
- [x] Tutorial screen
- [x] Source list screen
    - [x] Display news sources
    - [x] Sort news sources by ascending, descending orders
- [ ] News list screen
    - [x] Display articles of one particular source
    - [x] Add/remove article to/from favorites
    - [x] Filter articles by article category (politics, sports, business...)
    - [ ] Display votes
- [ ] News details screen
    - [x] Display one particular article
    - [x] Animated collapsing toolbar with image
    - [x] Button with openable link to original article
    - [x] Smooth screen transition when navigated from news list screen
    - [ ] Display votes
    - [ ] Small map with a single location (single pinned location, locked camera zoom, custom marker & tooltip)
    - [ ] Save article as .pdf to external storage
- [ ] Favorite screen
    - [x] Display all articles, marked as favorite
    - [x] Search for a particular article by title in toolbar
    - [ ] Display votes
- [ ] About screen
    - [ ] Take and display a camera selfie
- [ ] Maps screen
    - [ ] Large map with all locations (custom marker, cluster & tooltip)

### Other tasks
- [ ] Dark mode (missing: maps, captions)
- [x] Cache items to display in offline mode
- [x] Handle empty states
- [x] Navigation animations
- [x] Handle errors (show Snackbar)
- [x] Unit tests
- [ ] UI tests
- [ ] Add Google Maps with full customization
- [ ] Migrate to Rx (separate branch)
- [ ] Utilize GraphQL with SpaceX API
    - [ ] Get a `landpads` list from the API
    - [ ] Show the `landpads` list on a map using `latitude`, `longitude` variables
    - [ ] Show additional info on `landpad` pin clicked (`name`, `successfulLandings`, `attemptedLandings`, `successRatio`)
    - [ ] Clickable link to wiki page that opens browser 

### Requirements (libraries, tools, versions)

- Correct SDKs
    - [x] Minimum SDK is 24
    - [x] Target SDK is 33 [(the latest available, as of October 2022)](https://developer.android.com/studio/releases/platforms)
- [Design]((https://www.figma.com/file/VXiNfPRF9qFUtZFDv4TfEe))
    - [x] [Material Design](https://material.io/)
    - [x] [Material Design Guidelines](https://material.io/design)
    - [x] [Jetpack Compose](https://developer.android.com/jetpack/compose)
    - [x] Accompanist
- [Architecture](https://developer.android.com/topic/architecture)
    - [x] [MVVM](https://developer.android.com/topic/libraries/architecture/viewmodel)
- Concurrency
    - [x] [Flow](https://developer.android.com/kotlin/flow)
    - [x] [Coroutines](https://developer.android.com/kotlin/coroutines)
    - [ ] [Rx](https://github.com/ReactiveX/RxKotlin)
- [Dependency Injection](https://developer.android.com/training/dependency-injection)
    - [x] [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
- Network
    - REST
        - [x] [Retrofit](https://square.github.io/retrofit/)
        - [x] [Moshi](https://github.com/square/moshi)
        - [x] [Coil](https://coil-kt.github.io/coil/)
    - GraphQL
        - [ ] [Apollo](https://www.apollographql.com/docs/kotlin/)
        - [ ] [GraphQL plugin for Android Studio](https://plugins.jetbrains.com/plugin/8097-graphql)
        - [ ] [GraphQL SpaceX API](https://studio.apollographql.com/public/SpaceX-pxxbxen/explorer)
- [Testing](https://developer.android.com/training/testing/local-tests)
    - [x] JUnit
    - [x] [MockK](https://mockk.io/ANDROID.html)
    - [ ] [Espresso](https://developer.android.com/training/testing/espresso)
- [Data storage](https://developer.android.com/training/data-storage)
    - [x] [Room](https://developer.android.com/training/data-storage/room)
    - [x] [Preferences DataStore](https://developer.android.com/topic/libraries/architecture/datastore)
- Logging
    - [x] [Timber](https://github.com/JakeWharton/timber)
- Maps
    - [x] [Google Maps Compose](https://github.com/googlemaps/android-maps-compose)
    - [x] [Google Maps Utils](https://github.com/googlemaps/android-maps-utils)
- Other
    - [x] [LeakCanary](https://github.com/square/leakcanary/)
    - [x] [Firebase Crashlytics](https://firebase.google.com/docs/crashlytics)
    - [x] [Google Analytics for Firebase](https://firebase.google.com/docs/analytics/)
    - [x] [Firebase Cloud Messaging](https://firebase.google.com/docs/cloud-messaging/) 
