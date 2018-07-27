# React Native UVC Camera
A React Native component to access to UVC web camera on non-rooted Android device, a combination of [React Native Camera](https://github.com/react-native-community/react-native-camera) and [UVCCamera](https://github.com/saki4510t/UVCCamera) .

[UvcCameraManager](https://github.com/flyskywhy/UvcCameraManager) is an example.

## Installation (Android)

* Add `android/local.properties` , `android-ndk` need just `r14b` in [NDK Archives](https://developer.android.google.cn/ndk/downloads/older_releases), e.g.
```
sdk.dir=D\:\\proj\\tools\\android-sdk
ndk.dir=D\:\\proj\\tools\\android-ndk-r14b
```

* In `android/settings.gradle`

```
...
include ':react-native-uvc-camera'
project(':react-native-uvc-camera').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-uvc-camera/android')
include ':usbCameraCommon'
project(':usbCameraCommon').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-uvc-camera/usbCameraCommon')
include ':libuvccamera'
project(':libuvccamera').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-uvc-camera/libuvccamera')
include ':react-native-camera'
project(':react-native-camera').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-camera/android')
```

* In `android/app/build.gradle`

```
...
android {
    ...
    defaultConfig {
        ...
        minSdkVersion 18
        ...
    }
    ...
}
...
repositories {
    maven { url 'http://raw.github.com/saki4510t/libcommon/master/repository/' }
}

dependencies {
    compile project(':react-native-uvc-camera')
    compile ('com.facebook.react:react-native:0.51.0') { force = true }
    ...
}
```

* In `android/app/src/main/AndroidManifest.xml`
```
...
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-sdk
        android:minSdkVersion="18"
```
To enable `video recording` feature you have to add the following code:
```
    <uses-permission android:name="android.permission.RECORD_AUDIO"/>
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

* In `android/build.gradle`

```
...
ext {
    compileSdkVersion           = 26
    targetSdkVersion            = 26
    buildToolsVersion           = "26.0.2"
    googlePlayServicesVersion   = "12.0.1"
    supportLibVersion           = "27.1.0"
    versionBuildTool            = "26.0.2"
    versionCompiler             = 26
    versionTarget               = 26
    commonLibVersion            = "1.5.20"
    javaSourceCompatibility     = JavaVersion.VERSION_1_7
    javaTargetCompatibility     = JavaVersion.VERSION_1_7
}
```

## Usage
Take a look into this [RNCamera](https://github.com/react-native-community/react-native-camera/blob/master/docs/RNCamera.md) doc.

UvcCamera Properties additional to RNCamera:

#### `rotation`

Values: `0, 90, 180, or 270.`

Most USB cameras have different rotation by default. It adjusts your camera rotation by your own.
