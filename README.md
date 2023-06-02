# React Native UVC Camera

[![npm version](http://img.shields.io/npm/v/react-native-uvc-camera.svg?style=flat-square)](https://npmjs.org/package/react-native-uvc-camera "View this project on npm")
[![npm downloads](http://img.shields.io/npm/dm/react-native-uvc-camera.svg?style=flat-square)](https://npmjs.org/package/react-native-uvc-camera "View this project on npm")
[![npm licence](http://img.shields.io/npm/l/react-native-uvc-camera.svg?style=flat-square)](https://npmjs.org/package/react-native-uvc-camera "View this project on npm")
[![Platform](https://img.shields.io/badge/platform-android-989898.svg?style=flat-square)](https://npmjs.org/package/react-native-uvc-camera "View this project on npm")

A React Native component to access to UVC web camera on non-rooted Android device, a combination of [React Native Camera](https://github.com/react-native-community/react-native-camera) and [UVCCamera](https://github.com/saki4510t/UVCCamera) .

For `RN0.70.5`, [UvcCameraManager branch main](https://github.com/flyskywhy/UvcCameraManager/tree/main) is an example. Since [react-native-camera](https://github.com/react-native-camera/react-native-camera) is deprecated in favor of [react-native-vision-camera](https://github.com/mrousavy/react-native-vision-camera), now `react-native-uvc-camera@2` has merged all code in `react-native-camera@1.1.4`, so
```
npm uninstall react-native-camera
npm install react-native-uvc-camera
```

For `RN0.51.0`, [UvcCameraManager branch master](https://github.com/flyskywhy/UvcCameraManager/tree/master) is an example.
```
npm install react-native-uvc-camera@1.0.0 react-native-camera@1.1.4
```
and read `README.md` of `react-native-uvc-camera@1.0.0`.

## Installation (Android)

* Add `android/local.properties` , `android-ndk` need just `r14b` in [NDK Archives](https://developer.android.google.cn/ndk/downloads/older_releases), e.g.
```
# must be absolute path, otherwise will got
# `Task ':libuvccamera:ndkBuild' is not up-to-date because: Task has not declared any outputs despite executing actions`
ndk.dir=D\:\\proj\\tools\\android-ndk-r14b
```

* In `android/settings.gradle`

```
include ':usbCameraCommon'
project(':usbCameraCommon').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-uvc-camera/usbCameraCommon')
include ':libuvccamera'
project(':libuvccamera').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-uvc-camera/libuvccamera')
```

* In `android/app/src/main/AndroidManifest.xml`
```
...
    <uses-permission android:name="android.permission.CAMERA" />
```
To enable `video recording` feature you have to add the following code:
```
    <uses-permission android:name="android.permission.RECORD_AUDIO"/>
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

* In `android/build.gradle`, or in `android/settings.gradle` if ref to [fix Read timed out when compiling on Android](https://github.com/flyskywhy/ReactWebNative8Koa/commit/96fad3d9524e64fa309d0e72a4d9ad4808a1470f)

```
repositories {
    maven {
        url 'https://raw.githubusercontent.com/saki4510t/libcommon/master/repository'
        allowInsecureProtocol = true
    }
}
```

If `Could not get resource 'https://raw.github.com/saki4510t/libcommon/master/repository/com/serenegiant/common/1.5.21/common-1.5.21.aar'.` while compile, then you may need set proxy whicn can quickly access `github.com` in `~/.gradle/gradle.properties` e.g.:
```
#systemProp.https.proxyHost=192.168.19.49
#systemProp.https.proxyPort=1001
```

## Usage
Take a look into this [RNCamera](https://github.com/react-native-camera/react-native-camera/blob/v1.1.4/docs/RNCamera.md) doc.

UvcCamera Properties additional to RNCamera:

#### `rotation`

Values: `0, 90, 180, or 270.`

Most USB cameras have different rotation by default. It adjusts your camera rotation by your own.

## Customization
As said in `android/src/main/java/com/google/android/cameraview/CameraUvc.java` :

        public void onStartPreview(){
            // some USB camera will not return onPictureTaken and onVideoRecorded by uncomment some of below, test them by your own
            // updateAutoFocus();
            // updateFlash();
            // updateFocusDepth();
            // updateWhiteBalance();
            // updateZoom();
        }

## Donate
To support my work, please consider donate.

- ETH: 0xd02fa2738dcbba988904b5a9ef123f7a957dbb3e

- <img src="https://raw.githubusercontent.com/flyskywhy/flyskywhy/main/assets/alipay_weixin.png" width="500">
