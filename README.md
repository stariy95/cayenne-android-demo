# Demo Android app using Apache Cayenne ORM framework

This is a demo Android app that uses Cayenne ORM.
Cayenne 4.0.B2 is used in this demo.
Min Android SDK version is 19 (Android 4.4).

## Customizations done

Following parts where changed in order to launch Cayenne runtime:

* Custom resource locator implemented that handle Cayenne XML files loading from Android assets
* Network activity is explicitly allowed on UI thread (see AppModule class) as Cayenne temp-id generator lookups local host address.
* Custom SchemaUpdateStrategy implemented (see UpdateSchemaStrategy) that allows to compare and migrate DB structure on every start.

## Third-party dependencies

* Dagger 2.13 is used as a DI framework
* slf4j-android 1.7.25 - binding of slf4j to Android logging
* Additionally this project requires SNAPSHOT version of *sqldroid* SQLite JDBC driver.
You can clone https://github.com/SQLDroid/SQLDroid and build it from sources, no modifications required.

## Limitations

* This project is not compatible with proguard
