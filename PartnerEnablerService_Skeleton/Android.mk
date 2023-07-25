LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := technology.cariad.partnerenablerservice
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_CLASS := APPS
LOCAL_MODULE_SUFFIX := $(COMMON_ANDROID_PACKAGE_SUFFIX)
LOCAL_PRIVILEGED_MODULE := true
LOCAL_SYSTEM_MODULE := true
LOCAL_CERTIFICATE := platform
LOCAL_REQUIRED_MODULES := privapp_permissions_partnerenablerservice.xml
LOCAL_SRC_FILES := $(LOCAL_MODULE).apk
LOCAL_SDK_VERSION := system_current
include $(BUILD_PREBUILT)
#######################
include $(CLEAR_VARS)
LOCAL_MODULE := privapp_permissions_partnerenablerservice.xml
LOCAL_MODULE_CLASS := ETC
LOCAL_MODULE_PATH := $(TARGET_OUT_PRODUCT_ETC)/permissions
LOCAL_SRC_FILES := privapp_permissions_partnerenablerservice.xml
include $(BUILD_PREBUILT)
#########################################################################
