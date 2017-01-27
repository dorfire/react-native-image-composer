*Based on React Native Android Library Boilerplate.*

## Installing it as a library in your main project
There are many ways to do this, here's the way I do it:

1. Push it to **GitHub**.
2. Do `npm install --save git+https://github.com/MrToph/react-native-android-library-boilerplate.git` in your main project.
3. Link the library:
    * Add the following to `android/settings.gradle`:
        ```
        include ':react-native-android-library-boilerplate'
        project(':react-native-android-library-boilerplate').projectDir = new File(settingsDir, '../node_modules/react-native-android-library-boilerplate/android')
        ```

    * Add the following to `android/app/build.gradle`:
        ```xml
        ...

        dependencies {
            ...
            compile project(':react-native-android-library-boilerplate')
        }
        ```
    * Add the following to `android/app/src/main/java/**/MainApplication.java`:
        ```java
        package com.motivation;

        import io.cmichel.boilerplate.Package;  // add this for react-native-android-library-boilerplate

        public class MainApplication extends Application implements ReactApplication {

            @Override
            protected List<ReactPackage> getPackages() {
                return Arrays.<ReactPackage>asList(
                    new MainReactPackage(),
                    new Package()     // add this for react-native-android-library-boilerplate
                );
            }
        }
        ```
4. Simply `import/require` it by the name defined in your library's `package.json`:

    ```javascript
    import Boilerplate from 'react-native-android-library-boilerplate'
    Boilerplate.show('Boilerplate runs fine', Boilerplate.LONG)
    ```
5. You can test and develop your library by importing the `node_modules` library into **Android Studio** if you don't want to install it from _git_ all the time.