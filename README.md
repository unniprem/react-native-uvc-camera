# React Native UVC Camera
A React Native component to access to UVC web camera on non-rooted Android device, a combination of [React Native Camera](https://github.com/react-native-community/react-native-camera) and [UVCCamera](https://github.com/saki4510t/UVCCamera) .

[UvcCameraManager](https://github.com/flyskywhy/UvcCameraManager) is an example.

## Installation (Android)

* Add `android/local.properties` , `android-ndk` need just `r14b` in [NDK Archives](https://developer.android.google.cn/ndk/downloads/older_releases), e.g.
```
# must be absolute path, otherwise will got
# `Task ':libuvccamera:ndkBuild' is not up-to-date because: Task has not declared any outputs despite executing actions`
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

* In `android/build.gradle`

```
repositories {
    maven {
        url 'https://github.com/saki4510t/libcommon/blob/master/repository'
        allowInsecureProtocol = true
    }
}
```

## Usage
Take a look into this [RNCamera](https://github.com/react-native-community/react-native-camera/blob/master/docs/RNCamera.md) doc.

UvcCamera Properties additional to RNCamera:

#### `rotation`

Values: `0, 90, 180, or 270.`

Most USB cameras have different rotation by default. It adjusts your camera rotation by your own.

## Customization
As said in `android/src/main/java/org/reactnative/cameraview/CameraUvc.java` :

        public void onStartPreview(){
            // some USB camera will not return onPictureTaken and onVideoRecorded by uncomment some of below, test them by your own
            // updateAutoFocus();
            // updateFlash();
            // updateFocusDepth();
            // updateWhiteBalance();
            // updateZoom();
        }
