<div align="center">
<h1 align="center">
<img src="https://raw.githubusercontent.com/PKief/vscode-material-icon-theme/ec559a9f6bfd399b82bb44393651661b08aaf7ba/icons/folder-markdown-open.svg" width="100" />
<br>meteor-observer
</h1>
<h3>◦ Report meteor track on sky with Meteor-Observer!</h3>
<h3>◦ Developed with the software and tools listed below.</h3>

<p align="center">
<img src="https://img.shields.io/badge/Python-3776AB.svg?style&logo=Python&logoColor=white" alt="Python" />
<img src="https://img.shields.io/badge/Gradle-02303A.svg?style&logo=Gradle&logoColor=white" alt="Gradle" />
<img src="https://img.shields.io/badge/java-%23ED8B00.svg?style&logo=openjdk&logoColor=white" alt="java" />
</p>
<img src="https://img.shields.io/github/languages/top/bolidozor/meteor-observer?style&color=5D6D7E" alt="GitHub top language" />
<img src="https://img.shields.io/github/languages/code-size/bolidozor/meteor-observer?style&color=5D6D7E" alt="GitHub code size in bytes" />
<img src="https://img.shields.io/github/commit-activity/m/bolidozor/meteor-observer?style&color=5D6D7E" alt="GitHub commit activity" />
<img src="https://img.shields.io/github/license/bolidozor/meteor-observer?style&color=5D6D7E" alt="GitHub license" />
</div>

---

##  Table of Contents
- [ Table of Contents](#-table-of-contents)
- [ Overview](#-overview)
- [ Features](#-features)
- [ Project Structure](#project-structure)
- [ Modules](#modules)
- [ Getting Started](#-getting-started)
- [ Roadmap](#-roadmap)
- [ Contributing](#-contributing)
- [ License](#-license)
- [ Acknowledgments](#-acknowledgments)

---


##  Overview

The project Meteor-observer is an Android application designed for recording and analyzing meteor trajectories using the device's sensors. It primarily utilizes Android's location services, its accelerometer, and magnetometer for analyzing and recording the trajectories of smartphone motion. The application supports further functionalities such as adding, retrieving, updating and deleting related or shared data derived from the SQLite database, uploading the records to the servers in an ordered sequence, and providing user preference settings. This application, as a vital tool for astronomers pairs technology with meteor observations, possibly expanding understanding of celestial bodies through gathered data.

---

##  Features

| Feature                | Description                           |
| ---------------------- | ------------------------------------- |
| ** Architecture**     | Utilizing the GPS, magnetometer, and accelerometer, it's an event-driven Android application built with architecture pattern promoting high cohesion and low coupling.
| ** Documentation**   | The documentation isn't self-explanatory but has comments explaining certain complex functionalities. Overall, the idea can be gleaned but it's not thoroughly accessible.
| ** Dependencies**    | Core dependencies include ButterKnife for linking views, JodaTimeAndroid for date/time manipulations. It consistently uses SQLite for local data storage.
| ** Modularity**       | Organized into neat modules- UI components, utility classes, background tasks, data model classes for easy testing, debugging, and development.
| ** Performance**      | Judging by the AsyncTask usage, the application's restructuring for foreground-background operations should lead to a robust and good end-user experience.
| ** Integrations**    | Includes background tasks (AsyncTask) for extracting records and uploading to a server(process unidentifiable). Expected usage of Play Services (section as comments). 
| ** Scalability**     | SQLITE3 making local storage easier. But, without proper handling the database could fill resulting in slow data fetching due to size bloating. If planned to web-scatter, external hosting can help.

---


##  Project Structure




---

##  Modules

<details closed><summary>Root</summary>

| File                                                                                      | Summary                                                                                                                                                                                                                                                                                                                                                                                                            |
| ---                                                                                       | ---                                                                                                                                                                                                                                                                                                                                                                                                                |
| [build.gradle](https://github.com/bolidozor/meteor-observer/blob/main/build.gradle)       | This simplified top-level Android build script is for Gradle. It designates common repositories — both jcenter and Google's repository. It sets the Gradle classpath for tools needed. It configures several configurations to be universally applied across all modules including compiling software development kit (SDK) version, build tools version and specific versions for supportLib, gmsLib, and awsLib. |
| [gradlew.bat](https://github.com/bolidozor/meteor-observer/blob/main/gradlew.bat)         | This is a Windows startup script for Java-based Gradle builds system. It defines universal environmental variables and checks if'java.exe' exists in your path. If not found, a request is made to set JAVA_HOME variable. It fetches mentions for the command-line arguments, handles system-specific differences, then initializes and runs the Gradle project. In case of failure, an error is returned.        |
| [settings.gradle](https://github.com/bolidozor/meteor-observer/blob/main/settings.gradle) | The commented code is a dependency inclusion for Google Play Services in an Android project managed through Gradle. When uncommented, it allows the application to make use of Google services and APIs like Maps, Drive, and cloud messaging. Essentially, it expands app capabilities for enriched user interaction.                                                                                             |

</details>

<details closed><summary>App</summary>

| File                                                                                    | Summary                                                                                                                                                                                                                                                                                                                                                                                                                         |
| ---                                                                                     | ---                                                                                                                                                                                                                                                                                                                                                                                                                             |
| [build.gradle](https://github.com/bolidozor/meteor-observer/blob/main/app/build.gradle) | This code appears to be used for building an Android application with specific configuration details. It outlines minSdkVersion as 16, targetSdkVersion as 22, and sets its versionName to "1.1.2". Additionally, separate configurations for debug and release versions of the application are described. Provisions for Java compatibility issues are highlighted. Also, a list of required project dependencies is outlined. |

</details>

<details closed><summary>Model</summary>

| File                                                                                                                                      | Summary                                                                                                                                                                                                                                                                                                                                                                                                         |
| ---                                                                                                                                       | ---                                                                                                                                                                                                                                                                                                                                                                                                             |
| [Vector3.java](https://github.com/bolidozor/meteor-observer/blob/main/app/src/main/java/cz/expaobserver/model/Vector3.java)               | This Java code defines the Vector3 custom class for manipulating 3-dimensional vectors. Key functionalities include cross product, scalar multiplication, normalization, dot product, and addition of two vectors. It also implements Android's Parcelable interface, providing functions for sending and receiving Vector3 objects across inter-Process communications.                                        |
| [StampedVector3.java](https://github.com/bolidozor/meteor-observer/blob/main/app/src/main/java/cz/expaobserver/model/StampedVector3.java) | The code is for a class, StampedVector3, that extends Vector3. It adds a timestamp function to the Vector3 object for events/logs, while maintaining Vector3's primary properties and functionalities. Parts of the code integrate Android's Parcel system, enabling object serialization; making it possible to move complex objects between Android components.                                               |
| [RecordStore.java](https://github.com/bolidozor/meteor-observer/blob/main/app/src/main/java/cz/expaobserver/model/RecordStore.java)       | The code represents a model class "RecordStore" which performs various SQLite database operations. As a SQLiteOpenHelper, it opens, creates, and upgrades SQLite databases. Key functionalities include operations for adding, updating, or deleting records to and from the "records" table. It supports retrieving all records, constructing records from a result set, and querying records by specific IDs. |
| [Record.java](https://github.com/bolidozor/meteor-observer/blob/main/app/src/main/java/cz/expaobserver/model/Record.java)                 | This code defines a Record class used for storing geolocation data branching from two vectors (trailBeg & trailEnd) and notes. It includes fields for ID, time, location (latitude & longitude), and a note. It also provides for functionality to calculate angles from these vectors (azimuth & elevation angles).                                                                                            |

</details>

<details closed><summary>Util</summary>

| File                                                                                                                                   | Summary                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| ---                                                                                                                                    | ---                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| [DateUtils.java](https://github.com/bolidozor/meteor-observer/blob/main/app/src/main/java/cz/expaobserver/util/DateUtils.java)         | The DateUtils class in cz.expaobserver.util package primarily provides date time formatting functionality based on the'org.joda.time.format.DateTimeFormat' package. It's a utility class providing a constant'DATETIME_FORMATTER' in a specific pattern-"yyyy-MM-dd kk:mm:ss". It does not support instantiation.                                                                                                                                                                                                          |
| [ActivityUtils.java](https://github.com/bolidozor/meteor-observer/blob/main/app/src/main/java/cz/expaobserver/util/ActivityUtils.java) | The ActivityUtils class encapsulates utility functions which enhance user activity navigation and manage screen brightness settings. The `navigateUp` method assists hierarchical navigation within and across application tasks. It establishes and maneuvers up a task's back stack.Methods `setBrightness`, `setBrightnessAuto`, and `getBrightness` legally change screen brightness settings either manually or automate it based on system light conditions. Finally, errors are catch-ed and re-handled accordingly. |
| [Util.java](https://github.com/bolidozor/meteor-observer/blob/main/app/src/main/java/cz/expaobserver/util/Util.java)                   | The package encapsulates multiple utilities for Android applications. These comprise debugging functionalities, modifying and assigning application window colors and backgrounds, setting color-specific resources, fetching and mapping attributes for user-interface components like context-based menus, drawables, and hues. The code also considers platform version specific functionalities for inclusive compatibility. Furthermore, views across differing screen ratios are managed.                             |

</details>

<details closed><summary>Background</summary>

| File                                                                                                                                                 | Summary                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| ---                                                                                                                                                  | ---                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| [ObserverLogic.java](https://github.com/bolidozor/meteor-observer/blob/main/app/src/main/java/cz/expaobserver/background/ObserverLogic.java)         | This Android application code is for recording and observing the motion trajectories using phone's sensors. It manages sensor updates (magnetometer and accelerometer), records chronological vectors, measures and speculates transitions and anomalies. Further, it captures the GPS location data and prompts notifications. Current observations are stored in a record history with navigation trails mark starting, ending and states of trajectories. |
| [UploadRecordsTask.java](https://github.com/bolidozor/meteor-observer/blob/main/app/src/main/java/cz/expaobserver/background/UploadRecordsTask.java) | This Android-related code provides functionality to execute a background task (AsyncTask) for uploading records to a server in sequential order. These records, extracted from an application, are accessed via instances of the Record model. Progress is tracked and displayed using a ProgressDialog. The code also implements error handling, showing an alert dialog in case of upload failure.                                                         |

</details>

<details closed><summary>Ui</summary>

| File                                                                                                                                                             | Summary                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
| ---                                                                                                                                                              | ---                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| [ObserverFragment.java](https://github.com/bolidozor/meteor-observer/blob/main/app/src/main/java/cz/expaobserver/ui/ObserverFragment.java)                       | The provided Java code defines an `ObserverFragment` class extending Android's `Fragment`, implementing `ObserverLogic.Callbacks`. The class revolves around geographic location and state updates using `ObserverLogic`. Upon creation, it retrieves the lone ObserverLogic instance, which then starts/stops listening for updates as the fragment resumes/pauses. The class relays these updates (location, state, time, and orientation changes) to its attached activity.                                                      |
| [RecordListFragment.java](https://github.com/bolidozor/meteor-observer/blob/main/app/src/main/java/cz/expaobserver/ui/RecordListFragment.java)                   | The given code defines functionalities for an application that manages records. Key functions involve the ability to create, view, delete, update and upload records. These records can also have attached notes. Records are stored using the'RecordStore' module that is part of the main application in'ObserverApplication'. There's also functionality for selecting all records and attaching a note to selected records. The interfacing is performed through a list that has selectable elements represented by checkboxes. |
| [RecordListActivity.java](https://github.com/bolidozor/meteor-observer/blob/main/app/src/main/java/cz/expaobserver/ui/RecordListActivity.java)                   | This Android application code, specifically the RecordListActivity class, predominantly handles the user interface management for a certain record list activity on screen. Key features are the setup and destruction of display option menus, navigation, and transitions-including the home function.The bulk of single content presentation arranging-conveyed madly by Fragment attachment/detachment exists within this code's premise.                                                                                       |
| [ObserverActivity.java](https://github.com/bolidozor/meteor-observer/blob/main/app/src/main/java/cz/expaobserver/ui/ObserverActivity.java)                       | The code outlines an Android application focused on sensor-based observation and GPS tracking. Main functionalities include managing light, magnetic, and location sensors. It updates data about the device's location and orientation based on sensors and displays it via GUI. Furthermore, it offers adjustable settings via options menu, direct GPU settings, and screen dimming with system UI adjustments.                                                                                                                  |
| [ConfirmIntentDialogFragment.java](https://github.com/bolidozor/meteor-observer/blob/main/app/src/main/java/cz/expaobserver/ui/ConfirmIntentDialogFragment.java) | This code module encapsulates a `ConfirmIntentDialogFragment` utility for an Android Application. Its core function entails validating Intents before launching them. This validation identifies whether the Intent originates from the application package, or if it's externally pertinent. An AlertDialog box then takes user affirmation before the Intent starts. An extra field'EXTRA_INTENT_CONFIRMED' signals the confirmed Intent.                                                                                         |
| [ObserverApplication.java](https://github.com/bolidozor/meteor-observer/blob/main/app/src/main/java/cz/expaobserver/ui/ObserverApplication.java)                 | This Android application ('ObserverApplication') performs a variety of core functions. On creation, it sets the application instance, initializes the JodaTimeAndroid library for handling date and time, sets up a user ID, assigns default preferences, and initializes a'RecordStore' data holder.                                                                                                                                                                                                                               |
| [SettingsFragment.java](https://github.com/bolidozor/meteor-observer/blob/main/app/src/main/java/cz/expaobserver/ui/SettingsFragment.java)                       | This Android-based code features a'SettingsFragment' that manages preference settings of an application. The preferences are retrieved from an XML. There is on-change listener for'upload server' preference that saves user-selected preference. These preferences are editable only in debug mode. It also decorates the settings with a divider.                                                                                                                                                                                |
| [SettingsActivity.java](https://github.com/bolidozor/meteor-observer/blob/main/app/src/main/java/cz/expaobserver/ui/SettingsActivity.java)                       | This code is for a SettingsActivity in an Android app, executed when the settings view is initiated. It enables home-as-up navigation, and replaces the current content with a SettingsFragment instance. It also prepares the options menu by tinting it, and adds functionality for the home/up navigation.                                                                                                                                                                                                                       |

</details>

<details closed><summary>Widget</summary>

| File                                                                                                                                          | Summary                                                                                                                                                                                                                                                                                                                                                       |
| ---                                                                                                                                           | ---                                                                                                                                                                                                                                                                                                                                                           |
| [BrightnessView.java](https://github.com/bolidozor/meteor-observer/blob/main/app/src/main/java/cz/expaobserver/ui/widget/BrightnessView.java) | The code is a UI widget for handling brightness settings in Android. It uses the ButterKnife library to bind UI elements defined in XML to variables. The code features a single SeekBar and Button within a LinearLayout. The seeking status of the SeekBar can alter the visibility of UI elements. The view can be expanded/collapsed by user interaction. |

</details>

---

## Usage

 1. Remember the trajectory of a meteor. 
 2. Open application 
 3. Point at the beginning and end of the meteor trajectory
 4. Send the data to the Bolidozor network 

##  Contributing

Contributions are always welcome! Please follow these steps:
1. Fork the project repository. This creates a copy of the project on your account that you can modify without affecting the original project.
2. Clone the forked repository to your local machine using a Git client like Git or GitHub Desktop.
3. Create a new branch with a descriptive name (e.g., `new-feature-branch` or `bugfix-issue-123`).
```sh
git checkout -b new-feature-branch
```
4. Make changes to the project's codebase.
5. Commit your changes to your local branch with a clear commit message that explains the changes you've made.
```sh
git commit -m 'Implemented new feature.'
```
6. Push your changes to your forked repository on GitHub using the following command
```sh
git push origin new-feature-branch
```
7. Create a new pull request to the original project repository. In the pull request, describe the changes you've made and why they're necessary.
The project maintainers will review your changes and provide feedback or merge them into the main branch.

---

## License

This project is licensed under the GPL 3 License. See the [LICENSE](https://docs.github.com/en/communities/setting-up-your-project-for-healthy-contributions/adding-a-license-to-a-repository) file for additional info.



