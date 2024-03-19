# React Native UVC Camera

Library and sample to access to UVC web camera on non-rooted Android device.\
Works with latest Android versions! (API level 33 - Android 13 at the time of writing).\
Please see and test sample projects `usbCameraTest*`.

A React Native component to access to UVC web camera on non-rooted Android device, a combination of [React Native Camera](https://github.com/react-native-community/react-native-camera) and [UVCCamera](https://github.com/saki4510t/UVCCamera) .

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

All files in the folder are under this Apache License, Version 2.0.
Files in the jni/libjpeg, jni/libusb and jin/libuvc folders may have a different license,
see the respective files.

# How to compile library

The Gradle build system will build the entire project, including the NDK parts. If you want to build with Gradle build system,

1. make directory on your favorite place (this directory is parent directory of `UVCCamera` project).
2. change directory into the directory.
3. clone this repository with `git  clone https://github.com/saki4510t/UVCCamera.git`
4. change directory into `UVCCamera` directory with `cd UVCCamera`
5. build library with all sample projects using `gradle build`

It will takes several minutes to build. Now you can see apks in each `{sample project}/build/outputs/apks` directory.\
Or if you want to install and try all sample projects on your device, run `gradle installDebug`.\

Note: Just make sure that `local.properties` contains the paths for `sdk.dir` and `ndk.dir`. Or you can set them as enviroment variables of you shell. On some system, you may need add `JAVA_HOME` envairoment valiable that points to JDK directory.\

If you want to use Android Studio(unfortunately NDK supporting on Android Studio is very poor though),

1. make directory on your favorite place (this directory is parent directory of `UVCCamera` project).
2. change directory into the directory.
3. clone this repository with `git  clone https://github.com/saki4510t/UVCCamera.git`
4. start Android Studio and open the cloned repository using `Open an existing Android Studio project`
5. Android Studio raise some errors but just ignore now. Android Studio generate `local.properties` file. Please open `local.properties` and add `ndk.dir` key to the end of the file. The contents of the file looks like this.

```
npm uninstall react-native-camera
npm install react-native-uvc-camera
```

Please replace actual path to SDK and NDK on your storage.\
Of course you can make `local.properties` by manually instead of using automatically generated ones by Android Studio. 6. Synchronize project 7. execute `Make project` from `Build` menu.

For `RN0.51.0`, [UvcCameraManager branch master](https://github.com/flyskywhy/UvcCameraManager/tree/master) is an example.

```
npm install react-native-uvc-camera@1.0.0 react-native-camera@1.1.4
```

and read `README.md` of `react-native-uvc-camera@1.0.0`.

## Installation (Android)

- Add `android/local.properties` , `android-ndk` need just `r14b` in [NDK Archives](https://developer.android.google.cn/ndk/downloads/older_releases), e.g.

```
# must be absolute path, otherwise will got
# `Task ':libuvccamera:ndkBuild' is not up-to-date because: Task has not declared any outputs despite executing actions`
ndk.dir=D\:\\proj\\tools\\android-ndk-r14b
```

- In `android/settings.gradle`

```
include ':usbCameraCommon'
project(':usbCameraCommon').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-uvc-camera/usbCameraCommon')
include ':libuvccamera'
project(':libuvccamera').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-uvc-camera/libuvccamera')
```

- In `android/app/src/main/AndroidManifest.xml`

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

- In `android/build.gradle`, or in `android/settings.gradle` if ref to [fix Read timed out when compiling on Android](https://github.com/flyskywhy/ReactWebNative8Koa/commit/96fad3d9524e64fa309d0e72a4d9ad4808a1470f)

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

### 2015/07/19

Add new methods to get/set camera features like brightness, contrast etc.\
Add new method to get supported resolution from camera as json format.\

### 2015/08/17

Add new sample project `USBCameraTest7` to demonstrate how to use two camera at the same time.\

### 2015/09/20

Fixed the issue that building native libraries fail on Windows.

### 2015/10/30

Merge pull request(add status and button callback). Thanks Alexey Pelykh.

### 2015/12/16

Add feature so that user can request fps range from Java code when negotiating with camera. Actual resulted fps depends on each UVC camera. Currently there is no way to get resulted fps(will add on future).

### 2016/03/01

update minoru001 branch, experimentaly support streo camera.

### 2016/06/18

replace libjpeg-turbo 1.4.0 with 1.5.0

### 2016/11/17

apply bandwidth factor setting of usbcameratest7 on master branch

### 2016/11/21

Now this repository supports Android N(7.x) and dynamic permission model of Android N and later.

### 2017/01/16

Add new sample app `usbCameraTest8` to show how to set/get uvc control like brightness

### 2017/04/17

Add new sample app on [OpenCVwithUVC](https://github.com/saki4510t/OpenCVwithUVC.git) repository.
This shows the way to pass video images from UVC into `cv::Mat` (after optional applying video effect by OpenGL|ES) and execute image processing by `OpenCV`.

### 2020/07/24

- 添加根据摄像头自身角度选择图像处理
- 添加水平镜像处理
- 添加垂直镜像处理
- 添加是否丢弃不完整帧
- 添加可设置帧缓存大小
- 修复颜色转换出错问题
