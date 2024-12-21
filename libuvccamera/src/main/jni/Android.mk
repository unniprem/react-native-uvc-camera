#include $(call all-subdir-makefiles)
PROJ_PATH	:= $(call my-dir)
include $(CLEAR_VARS)
include $(PROJ_PATH)/UVCCamera/Android.mk
include $(PROJ_PATH)/libjpeg-turbo-1.5.0/Android.mk
include $(PROJ_PATH)/libusb/android/jni/Android.mk
include $(PROJ_PATH)/libuvc/android/jni/Android.mk
ifneq ($(TARGET_ARCH_ABI), mips)
    # Include the libjpeg-turbo sources
    include $(CLEAR_VARS)
    LOCAL_MODULE := jpeg-turbo1500_static
    LOCAL_SRC_FILES := jdhuff.c jchuff.c jdphuff.c
    include $(BUILD_STATIC_LIBRARY)
endif
